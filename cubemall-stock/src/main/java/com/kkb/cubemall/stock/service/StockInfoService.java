package com.kkb.cubemall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.stock.entity.StockInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author it
 * @email it@gmail.com
 * @date 2021-06-01 15:14:10
 */
public interface StockInfoService extends IService<StockInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

