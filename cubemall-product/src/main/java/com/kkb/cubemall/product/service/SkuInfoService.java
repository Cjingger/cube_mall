package com.kkb.cubemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.product.entity.SkuInfoEntity;
import com.kkb.cubemall.product.vo.SkuItemVo;
import com.kkb.cubemall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * sku??Ϣ
 *
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-19 18:24:09
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * @Description: 商品详情页加载接口
     */
    SkuItemVo getSkuItem(Long SkuId);
}

