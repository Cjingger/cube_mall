package com.cubemall.search.controller;


import com.cubemall.search.model.Blog;
import com.kkb.cubemall.common.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @PostMapping("/blog")
    public R postBlog(@RequestBody Blog blog){
        return R.ok(blog);
    }

    @PostMapping("/blog")
    public R postBlog(@RequestBody Blog blog, String name){
        blog.setContent(name);
        blog.setMobile("11111");
        return R.ok(blog);
    }
}
