package com.cubemall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.xml.ws.soap.Addressing;
import java.io.FileWriter;

@Controller
public class HelloController2 {
    //使用springTemplateEngine,生成静态页面
    @Autowired
    private SpringTemplateEngine engine;

    @GetMapping("/hello")
    //默认使用springMVC的视图解析器解析
    public String Hello(Model model){
        //向model对象中添加模板使用的变量
        model.addAttribute("hello", "hello world!");
        model.addAttribute("html", "<h2>this is a html</h2>");;
        model.addAttribute("flag",false);
        //和JSP一样,返回模板文件名称即可
        return "hello";
    }
}
