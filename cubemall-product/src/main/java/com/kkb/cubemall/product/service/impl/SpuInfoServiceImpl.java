package com.kkb.cubemall.product.service.impl;

import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.*;
import com.kkb.cubemall.product.exception.RemoteServiceCallExeption;
import com.kkb.cubemall.product.feign.SearchFeign;
import com.kkb.cubemall.product.service.*;
import com.kkb.cubemall.product.vo.*;
import feign.QueryMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kkb.cubemall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    BrandService brandService;

    @Autowired
    SearchFeign searchFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 对七张数据库表保存操作
     * @param spuSaveVo
     */
    @Override
    //出现异常事务回滚
    @Transactional(rollbackFor = MethodArgumentNotValidException.class)
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //保存tb_spu_info spu基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //保存tb_spu_info_desc spu描述信息
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setDecript(spuSaveVo.getDecript());
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //保存tb_spu_images spu图片集信息
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImage(spuInfoEntity.getId(), images);

        //保存tb_product_attr_value spu规格参数
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId((long) attr.getAttrId());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(collect);

        //保存sku对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();

        if (skus != null && skus.size()>0){
            skus.forEach(sku -> {
                //获取默认图片
                String defaultImg = "";
                for(Images image : sku.getImages()){
                    if (image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //-1保存tb_sku_info sku基本信息
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCategoryId(spuInfoEntity.getCategoryId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.saveSkuInfo(skuInfoEntity);
                //-2保存tb_sku_images sku图片信息
                Long skuId = skuInfoEntity.getId();
                List<SkuImagesEntity> imagesEntities = sku.getImages().stream().map(
                        image -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuId);
                            skuImagesEntity.setImgUrl(image.getImgUrl());
                            skuImagesEntity.setDefaultImg(image.getDefaultImg());
                            return skuImagesEntity;

                        }
                ).filter(image->{
                    //筛选当前图片路径是否为空
                    return StringUtils.isEmpty(image.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                //-3保存tb_sku_sale_attr_value sku销售属性
                List<Attr> attrs = sku.getAttr();
                if (attrs != null && attrs.size()>0){
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(
                            attr -> {
                                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                                BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                                skuSaleAttrValueEntity.setSkuId(skuId);
                                return skuSaleAttrValueEntity;
                            }
                    ).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities  );
                }

            });
        }
    }

    /**
     * spu条件查询
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //是否携带key
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                    w.eq("id", key).or().like("spu_name", key);
                    });
        }
        //是否携带分类ID,品牌ID
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String categoryId = (String) params.get("categoryId");
        if(!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)){
            wrapper.eq("category_id", categoryId);
        }
        //是否携带status
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("public_status", status);
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);

    }

    @Override
    @Transactional(rollbackFor = RemoteServiceCallExeption.class)
    public R onSale(Long spuId) throws RemoteServiceCallExeption{
        //1 接受controller的传参spuId
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //2 根据spuId找到对应商品 修改商品状态
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        baseMapper.updateById(spuInfoEntity);
        //3 将商品添加到索引库 调用search工程的服务
        try {

            searchFeign.onSale(spuId);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RemoteServiceCallExeption("服务调用失败：商品上架失败");
        }
        //4 返回结果
        return R.ok("商品上架成功");
    }


    /**
     * 保存spu基本信息
     * @param spuInfoEntity
     */
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }


    /**
     * 根据skuId查询sku信息
     * @param skuId
     */
    public R getSpuInfoBySkuId(Long skuId){
        //根据spuId找到sku实体
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        //根据sku实体获取spuId
        Long spuId = skuInfoEntity.getSpuId();
        //根据spuId查询spu数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);
        //查询品牌名称
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());

        //创建对象
        SpuInfoVo spuInfoVo = new SpuInfoVo();
        spuInfoVo.setBrandName(brandEntity.getName());
        spuInfoVo.setCategoryId(spuInfoEntity.getCategoryId());
        spuInfoVo.setId(spuInfoEntity.getId());
        spuInfoVo.setUpdateTime(spuInfoEntity.getUpdateTime());
        spuInfoVo.setCreateTime(spuInfoEntity.getCreateTime());
        spuInfoVo.setSpuName(spuInfoEntity.getSpuName());
        spuInfoVo.setSpuDescription(spuInfoEntity.getSpuDescription());
        spuInfoVo.setPublishStatus(spuInfoEntity.getPublishStatus());
        spuInfoVo.setWeight(spuInfoEntity.getWeight());

        return R.ok().setData(spuInfoVo);

    }
}