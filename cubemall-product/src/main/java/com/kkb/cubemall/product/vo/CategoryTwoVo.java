package com.kkb.cubemall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTwoVo {

    private String id;

    private String name;

    //一级分类id
    private String categoryId;

    //关联的三级分类
    private List<CategoryThreeVo> categoryThreeVos;
}
