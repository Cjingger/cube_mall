package com.kkb.cubemall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-28 18:50:53
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

