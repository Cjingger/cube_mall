package com.kkb.cubemall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.OrderHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-28 18:50:53
 */
public interface OrderHistoryService extends IService<OrderHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

