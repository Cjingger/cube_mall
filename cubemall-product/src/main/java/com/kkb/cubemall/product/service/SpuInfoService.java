package com.kkb.cubemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.SpuInfoEntity;
import com.kkb.cubemall.product.exception.RemoteServiceCallExeption;
import com.kkb.cubemall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-19 18:24:09
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    R onSale(Long spuId) throws RemoteServiceCallExeption;

    R getSpuInfoBySkuId(Long skuId);
}

