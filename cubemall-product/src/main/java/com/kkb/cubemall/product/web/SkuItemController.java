package com.kkb.cubemall.product.web;

import com.kkb.cubemall.product.service.SkuInfoService;
import com.kkb.cubemall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class SkuItemController {

    @Autowired
     private SkuInfoService skuInfoService;
    /**
     * @Description: 根据skuId查询商品详情信息
     */
    @GetMapping("/{skuId}.html")
    public String getSkuItem(@PathVariable Long skuId, Model model){
        SkuItemVo skuItem = skuInfoService.getSkuItem(skuId);
        //日志
        log.info("查询数据: {}", skuItem);
        //把数据放入模型驱动
        model.addAttribute("item", skuItem);
        return "item";

    }
}
