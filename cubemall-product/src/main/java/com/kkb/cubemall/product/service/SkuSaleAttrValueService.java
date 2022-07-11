package com.kkb.cubemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.product.entity.SkuSaleAttrValueEntity;
import com.kkb.cubemall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性值
 *
 * @author
 * @email @gmail.com
 * @date 2021-04-19 18:24:09
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    public PageUtils queryPage(Map<String, Object> params);

    public List<SkuItemSaleAttrVo> getSaleAttrValues(Long spuId);

}

