package com.kkb.cubemall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-28 18:50:54
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

