package com.kkb.cubemall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.config.MyThreadConfig;
import com.kkb.cubemall.product.entity.SkuImagesEntity;
import com.kkb.cubemall.product.entity.SpuInfoDescEntity;
import com.kkb.cubemall.product.entity.SpuInfoEntity;
import com.kkb.cubemall.product.feign.SeckillFeign;
import com.kkb.cubemall.product.service.*;
import com.kkb.cubemall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.SkuInfoDao;
import com.kkb.cubemall.product.entity.SkuInfoEntity;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private ThreadPoolExecutor poolExecutor;

    @Autowired
    private SeckillFeign seckillFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存tb_sku_info sku基本信息
     * @param skuInfoEntity
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    /**
     * sku条件查询
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        //是否携带key
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                    w.eq("id", key).or().like("sku_name", key);
            });
        }
        //是否携带categoryId, brandId
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String categoryId = (String) params.get("categoryId");
        if(!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)){
            wrapper.eq("category_id", categoryId);
        }
        //是否携带价格区间参数
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)){
            wrapper.ge("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(min) && !"0".equalsIgnoreCase(max)){
            wrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);


    }

    @Override
    public SkuItemVo getSkuItem(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        //开启异步编排,提升服务性能
        //根据skuId查询sku基本信息
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //获取sku基本信息
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, poolExecutor);

        //获取sku图片信息,根据skuId查询图片信息(多个),skuId为外键
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
                List<SkuImagesEntity> skuImages = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
                skuItemVo.setImages(skuImages);
        }, poolExecutor);


        //获取分类Id
//        Long categoryId = skuInfoEntity.getCategoryId();
        //根据spuId,获取spu销售属性
        CompletableFuture<Void> salesFuture = infoFuture.thenAcceptAsync((res) -> {
            //获取sku相对应的spuId
            Long spuId = res.getSpuId();
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrValues(spuId);
            if (saleAttrVos.size() > 0){
                skuItemVo.setAttrSales(saleAttrVos);
            }

        }, poolExecutor);

        CompletableFuture<Void> seckillFuture = infoFuture.thenAcceptAsync((res) -> {
            R seckillRedisTo = seckillFeign.getSeckillRedisTo(skuId);
            //查询skuId对应的秒杀商品详情
            if (seckillRedisTo.getCode() == 0){
                SeckillSkuVo seckillSkuVo = seckillRedisTo.getData("data", new TypeReference<SeckillSkuVo>(){});
                if (seckillSkuVo != null){
                    seckillSkuVo.setSkuId(skuId);
                }
            }

        }, poolExecutor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //获取sku相对应的spuId
            Long spuId = res.getSpuId();
            //根据spuId,获取sku描述信息
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
            if (spuInfoDescEntity != null){
                skuItemVo.setDesc(spuInfoDescEntity);
            }

        }, poolExecutor);
        //根据spuId,categoryId查询sku分组规格参数属性值
        CompletableFuture<Void> groupFuture = infoFuture.thenAcceptAsync((res) -> {
            //获取sku相对应的spuId
            Long spuId = res.getSpuId();
            //获取sku相对应的categoryId
            Long categoryId = res.getCategoryId();
            //根据spuId和categoryId,获取sku描述信息
            List<SpuAttrGroupVo> attrGroupVos = attrGroupService.getGroupAttr(spuId, categoryId);
            if (attrGroupVos.size() > 0){
                skuItemVo.setSpuAttrGroups(attrGroupVos);
            }
        }, poolExecutor);

        //等待所有的任务结束后,才能返回结果
        try {
            CompletableFuture.allOf(infoFuture,imageFuture,salesFuture,descFuture,groupFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return skuItemVo;
    }


}