package com.kkb.cubemall.product.feign;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.Blog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingDeque;

/**
 * @Author: jingger
 * @Date: 2021/8/30
 */
@FeignClient("cubemall-search")
public interface SearchFeign {
    @GetMapping("hello/{name}")
    public R Hello(@PathVariable("name") String name);

    @PostMapping("/blog")
    public R postBlog(@RequestBody Blog blog, @RequestParam("name") String name);

    @GetMapping("/spuinfo/onSale/{spuId}")
    public R onSale(@PathVariable("spuId") Long spuId);
}
