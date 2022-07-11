package com.kkb.cubemall.product.vo;

import com.kkb.cubemall.product.entity.SkuImagesEntity;
import com.kkb.cubemall.product.entity.SkuInfoEntity;
import com.kkb.cubemall.product.entity.SpuInfoDescEntity;
import com.kkb.cubemall.product.entity.SpuInfoEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 *
 */
@ToString
@Data
public class SkuItemVo {

    //sku基本信息
    private SkuInfoEntity info;

    //sku图片信息
    private List<SkuImagesEntity> images;

    //spu销售属性组合
    private List<SkuItemSaleAttrVo> attrSales;

    //spu描述信息
    private SpuInfoDescEntity desc;

    //spu分组(主体,基本信息...) 规格属性
    private List<SpuAttrGroupVo> spuAttrGroups;

    //商品秒杀信息
    private SeckillSkuVo seckillSkuVo;


}
