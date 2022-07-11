package com.cubemall.search.service.impl;

import com.cubemall.search.dao.SpuInfoDao;
import com.cubemall.search.service.SpuInfoService;
import com.kkb.cubemall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class SpuInfoServiceImpl implements SpuInfoService {
    @Autowired
    private SpuInfoDao spuInfoDao;

    @Override
    public R onSale(long spuId) {
        return null;
    }

    @Override
    public R syncSpuInfo() {
        return null;
    }

    @Override
    public Map<String, Object> search(Map<String, String> param) {
        return null;
    }
}
