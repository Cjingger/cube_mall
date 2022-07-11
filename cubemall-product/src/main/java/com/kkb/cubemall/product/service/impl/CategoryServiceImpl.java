package com.kkb.cubemall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kkb.cubemall.product.vo.CategoryThreeVo;
import com.kkb.cubemall.product.vo.CategoryTwoVo;
import com.kkb.cubemall.product.vo.CategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;

import com.kkb.cubemall.product.dao.CategoryDao;
import com.kkb.cubemall.product.entity.CategoryEntity;
import com.kkb.cubemall.product.service.CategoryService;
import org.thymeleaf.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

     private RedisTemplate redisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listTree() {
        //获取所有的菜单
        //
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //获取分类的一级菜单
        List<CategoryEntity> firstLevelMenus = categoryEntities.stream().filter(categoryEntity ->
            categoryEntity.getParentId() == 0 //如果父节点ID等于0 就是一级菜单
        ).map(menu->{
            menu.setChildrens(getChildrens(menu,categoryEntities)); //设置当前节点子节点
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSeq()==null?0:menu1.getSeq())-(menu2.getSeq()==null?0:menu2.getSeq());
        }).collect(Collectors.toList());
        return firstLevelMenus;
    }

    /**
     * 批量删除(逻辑)
     * @param asList
     */
    @Override
    public void removeNodesByIds(List<Integer> asList) {
        baseMapper.deleteBatchIds(asList);
    }


    /**
     * 根据子节点id，获取节点路径(父类节点ID)
     * @param categoryId
     * @return
     */
    @Override
    public Integer[] findCategoryPath(Integer categoryId) {
        List<Integer> paths = new ArrayList<>();
        List<Integer> parentPath = this.findParentPath(categoryId,paths);
        //颠倒顺序
        Collections.reverse(parentPath);
        return parentPath.toArray(new Integer[parentPath.size()]);
    }

    /**
     * redis缓存
     * @return
     */
    @Override
    public List<CategoryVo> getLevelCategorys() {
        //缓存中查数据
        BoundValueOperations<Object, Object> student_redission = redisTemplate.boundValueOps("student_redission");
        Object student_session = student_redission.get();
        if (student_redission == null){
            //第二个请求放入阻塞队列
            synchronized (this){
                student_session = student_redission.get();

            }
        }

        String categoryJSON = (String) redisTemplate.opsForValue().get("categoryJSON");
        if (StringUtils.isEmpty(categoryJSON)){
            List<CategoryVo> categoryVoList = this.getCategoryJsonFromDb();
            //查询数据放入缓存,对象转换为json
            redisTemplate.opsForValue().set("categoryJSON", JSON.toJSON(categoryVoList));
            return categoryVoList;
        }
        //缓存中有数据,将json数据转为对象
        return JSON.parseObject(categoryJSON , new TypeReference<List<CategoryVo>>(){});
    }

    /**
     * 缓存未命中,查询数据库中三级分类(分布式锁)
     * @return
     */
    public List<CategoryVo> getCategoryJsonFromRedisNx() {
        String uuid = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (locked){
            List<CategoryVo> categoryVoList = null;
            try{
                //加锁成功,执行业务
                categoryVoList = getCategoryJsonFromDb();
            }finally {
                //lua脚本
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Integer>(script, Integer.class), Arrays.asList("lock"), uuid);
            }
//            //为指定key设置过期时间
//            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
//            //删除锁
//            String keyValue = Objects.requireNonNull(redisTemplate.opsForValue().get("lock")).toString();
//            if (uuid.equals(keyValue)){
//                redisTemplate.delete("lock");
//            }
            return categoryVoList;

        }else {
            //加锁失败,重试
            return getCategoryJsonFromRedisNx();
        }
    }

    /**
     * 缓存未命中,查询数据库中三级分类(本地锁)
     * @return
     */
    public List<CategoryVo> getCategoryJsonFromDb() {
        synchronized (this){
            String categoryJSON = (String) redisTemplate.opsForValue().get("categoryJSON");
            //缓存命中
            if (!StringUtils.isEmpty(categoryJSON)){
                List<CategoryVo> categoryVoList = JSON.parseObject(categoryJSON, new TypeReference<List<CategoryVo>>(){});
                return categoryVoList;
            }
            System.out.println("查询数据库....");
            //查询所有分类
            List<CategoryEntity> allCategoryEntities = this.baseMapper.selectList(null);
            List<CategoryEntity> oneCategoryEntities = getParent_id(allCategoryEntities, 0);
            List<CategoryVo> categoryVoList = oneCategoryEntities.stream().map(categoryEntity ->{
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties(categoryEntity, categoryVo);
                //查询一级分类对应的二级分类信息
                List<CategoryEntity> twoCategoryEntities = getParent_id(allCategoryEntities, categoryEntity.getId());
                if (twoCategoryEntities != null){
                    //二级分类封装成vo
                    List<CategoryTwoVo> categoryTwoVoList = twoCategoryEntities.stream().map(categoryEntity2 ->{
                        CategoryTwoVo categoryTwoVo = new CategoryTwoVo();
                        categoryTwoVo.setId(categoryEntity2.getId().toString());
                        categoryTwoVo.setName(categoryEntity2.getName());
                        categoryTwoVo.setCategoryId(categoryEntity.getId().toString());
                        //二级分类下的三级分类
                        List<CategoryEntity> threeCategoryEntities = getParent_id(allCategoryEntities, categoryEntity2.getParentId());
                        if (threeCategoryEntities != null){
                            //二级分类封装成vo
                            List<CategoryThreeVo> categoryThreeVoList = twoCategoryEntities.stream().map(categoryEntity3 -> {
                                CategoryThreeVo categoryThreeVo = new CategoryThreeVo();
                                categoryThreeVo.setId(categoryEntity3.getId().toString());
                                categoryThreeVo.setName(categoryEntity3.getName());
                                categoryThreeVo.setCategoryTwoId(categoryEntity2.getId().toString());
                                return categoryThreeVo;
                            }).collect(Collectors.toList());
                        }
                        return categoryTwoVo;
                    }).collect(Collectors.toList());
                    categoryVo.setCategoryTwoVos(categoryTwoVoList);
                }
                return categoryVo;
            }).collect(Collectors.toList());

            //查询出的数据放入缓存中,设置过期时间(一天)
            redisTemplate.opsForValue().set("categoryJSON", JSON.toJSONString(categoryVoList), 1, TimeUnit.DAYS);

            return categoryVoList;
        }
    }

//    /**
//     * 获取一二三级分类
//     * @return
//     */
//    @Override
//    public List<CategoryVo> getLevelCategorys() {
//        //查询所有分类
//        List<CategoryEntity> allCategoryEntities = this.baseMapper.selectList(null);
//        List<CategoryEntity> oneCategoryEntities = getParent_id(allCategoryEntities, 0);
//        List<CategoryVo> categoryVoList = oneCategoryEntities.stream().map(categoryEntity ->{
//            CategoryVo categoryVo = new CategoryVo();
//            BeanUtils.copyProperties(categoryEntity, categoryVo);
//            //查询一级分类对应的二级分类信息
//            List<CategoryEntity> twoCategoryEntities = getParent_id(allCategoryEntities, categoryEntity.getId());
//            if (twoCategoryEntities != null){
//                //二级分类封装成vo
//                List<CategoryTwoVo> categoryTwoVoList = twoCategoryEntities.stream().map(categoryEntity2 ->{
//                    CategoryTwoVo categoryTwoVo = new CategoryTwoVo();
//                    categoryTwoVo.setId(categoryEntity2.getId().toString());
//                    categoryTwoVo.setName(categoryEntity2.getName());
//                    categoryTwoVo.setCategoryId(categoryEntity.getId().toString());
//                    //二级分类下的三级分类
//                    List<CategoryEntity> threeCategoryEntities = getParent_id(allCategoryEntities, categoryEntity2.getParentId());
//                    if (threeCategoryEntities != null){
//                        //二级分类封装成vo
//                        List<CategoryThreeVo> categoryThreeVoList = twoCategoryEntities.stream().map(categoryEntity3 -> {
//                            CategoryThreeVo categoryThreeVo = new CategoryThreeVo();
//                            categoryThreeVo.setId(categoryEntity3.getId().toString());
//                            categoryThreeVo.setName(categoryEntity3.getName());
//                            categoryThreeVo.setCategoryTwoId(categoryEntity2.getId().toString());
//                            return categoryThreeVo;
//                        }).collect(Collectors.toList());
//                    }
//                    return categoryTwoVo;
//                }).collect(Collectors.toList());
//                categoryVo.setCategoryTwoVos(categoryTwoVoList);
//            }
//            return categoryVo;
//        }).collect(Collectors.toList());
//        return categoryVoList;
//    }

    private List<CategoryEntity> getParent_id(List<CategoryEntity> allCategoryEntities, Integer parentId) {
        List<CategoryEntity> collect = allCategoryEntities.stream().filter(categoryEntity -> {
            return categoryEntity.getParentId().equals(parentId);
        }).collect(Collectors.toList());
        return collect;

    }

    /**
     * 查询父节点ID
     * id 当前节点ID
     * @return
     */
    private List<Integer> findParentPath(Integer id,List<Integer> paths){
        paths.add(id);
        //根据id查询当前bean对象
        CategoryEntity categoryEntity = this.getById(id);
        if(categoryEntity.getParentId()!=0){ //如果不为0，表示有父节点
            findParentPath(categoryEntity.getParentId(),paths);
        }
        //返回包含节点id的集合
        return paths;
    }

    /**
     * 获取当前节点的子节点
     * @param first
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity first,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentId().equals(first.getId()); //当前节点ID等于子节点的父ID
        }).map(categoryEntity -> {
            categoryEntity.setChildrens(getChildrens(categoryEntity,all)); //递归查询
            return categoryEntity;
        }).collect(Collectors.toList());

        return children;
    }



}
