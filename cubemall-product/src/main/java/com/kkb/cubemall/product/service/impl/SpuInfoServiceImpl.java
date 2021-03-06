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
     * ?????????????????????????????????
     * @param spuSaveVo
     */
    @Override
    //????????????????????????
    @Transactional(rollbackFor = MethodArgumentNotValidException.class)
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //??????tb_spu_info spu????????????
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //??????tb_spu_info_desc spu????????????
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setDecript(spuSaveVo.getDecript());
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //??????tb_spu_images spu???????????????
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImage(spuInfoEntity.getId(), images);

        //??????tb_product_attr_value spu????????????
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

        //??????sku???????????????sku??????
        List<Skus> skus = spuSaveVo.getSkus();

        if (skus != null && skus.size()>0){
            skus.forEach(sku -> {
                //??????????????????
                String defaultImg = "";
                for(Images image : sku.getImages()){
                    if (image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //-1??????tb_sku_info sku????????????
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCategoryId(spuInfoEntity.getCategoryId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.saveSkuInfo(skuInfoEntity);
                //-2??????tb_sku_images sku????????????
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
                    //????????????????????????????????????
                    return StringUtils.isEmpty(image.getImgUrl());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                //-3??????tb_sku_sale_attr_value sku????????????
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
     * spu????????????
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //????????????key
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w->{
                    w.eq("id", key).or().like("spu_name", key);
                    });
        }
        //??????????????????ID,??????ID
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String categoryId = (String) params.get("categoryId");
        if(!StringUtils.isEmpty(categoryId) && !"0".equalsIgnoreCase(categoryId)){
            wrapper.eq("category_id", categoryId);
        }
        //????????????status
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
        //1 ??????controller?????????spuId
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        //2 ??????spuId?????????????????? ??????????????????
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        baseMapper.updateById(spuInfoEntity);
        //3 ??????????????????????????? ??????search???????????????
        try {

            searchFeign.onSale(spuId);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RemoteServiceCallExeption("???????????????????????????????????????");
        }
        //4 ????????????
        return R.ok("??????????????????");
    }


    /**
     * ??????spu????????????
     * @param spuInfoEntity
     */
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }


    /**
     * ??????skuId??????sku??????
     * @param skuId
     */
    public R getSpuInfoBySkuId(Long skuId){
        //??????spuId??????sku??????
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        //??????sku????????????spuId
        Long spuId = skuInfoEntity.getSpuId();
        //??????spuId??????spu??????
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);
        //??????????????????
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());

        //????????????
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