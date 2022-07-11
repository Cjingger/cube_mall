package com.kkb.cubemall.product.feign;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.Blog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("cubemall-seckill")
public interface SeckillFeign {

    // 查询秒杀详情信息
    @GetMapping("/sku/seckill/{skuId}")
    public R getSeckillRedisTo(@PathVariable("skuId") Long skuId);

}
