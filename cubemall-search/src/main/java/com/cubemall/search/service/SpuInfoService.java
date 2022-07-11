package com.cubemall.search.service;

import com.kkb.cubemall.common.utils.R;

import java.util.Map;

public interface SpuInfoService {

    public R onSale(long spuId);
    public R syncSpuInfo();
    public Map<String, Object> search(Map<String, String> param);
}
