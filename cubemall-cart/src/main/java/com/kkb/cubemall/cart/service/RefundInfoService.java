package com.kkb.cubemall.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.auth.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-05-28 18:50:53
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

