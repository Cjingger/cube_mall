package com.kkb.cubemall.product.controller;

import com.kkb.cubemall.product.entity.CategoryEntity;
import com.kkb.cubemall.product.service.CategoryService;
import com.kkb.cubemall.product.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 首页展示
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){
        List<CategoryVo> categoryOneList = categoryService.getLevelCategorys();
        model.addAttribute("categorys", categoryOneList);
        return "item";
    }
}
