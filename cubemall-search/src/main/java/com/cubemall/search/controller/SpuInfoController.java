package com.cubemall.search.controller;

import com.cubemall.search.service.SpuInfoService;
import com.kkb.cubemall.common.utils.R;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spuinfo")
public class SpuInfoController {

    private volatile boolean executeFlag = false;

    @Autowired
    SpuInfoService spuInfoService;

    @GetMapping("onSale/{spuId}")
    public R onSale(@PathVariable Long spuId){

        return spuInfoService.onSale(spuId);
    }

    /**
     * 商品数据全量同步
     * @return
     */
    @GetMapping("syncSpuInfo")
    public R syncSpuInfo(){
        if (!executeFlag){
            synchronized (this){
                if (!executeFlag){
                    executeFlag = true;
                    return spuInfoService.syncSpuInfo();
                }
            }
        }
        return R.ok("数据正导入,请勿重复执行");

    }

}
