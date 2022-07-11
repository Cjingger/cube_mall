package com.kkb.cubemall.product.controller;


import com.kkb.cubemall.product.service.SkuInfoService;
import com.kkb.cubemall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * @Description: 根据skuId查询商品详情信息
     */
    @GetMapping("/{}.html")
    public SkuItemVo getSkuItem(@PathVariable Long skuId){
        SkuItemVo skuItem = skuInfoService.getSkuItem(skuId);
        return skuItem;

    }

}
