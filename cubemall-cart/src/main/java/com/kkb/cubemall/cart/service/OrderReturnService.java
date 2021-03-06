package com.kkb.cubemall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.OrderReturnEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-28 18:50:53
 */
public interface OrderReturnService extends IService<OrderReturnEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

