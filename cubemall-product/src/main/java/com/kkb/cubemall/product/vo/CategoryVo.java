package com.kkb.cubemall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 一级分类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryVo {

    private String id;

    private String name;

    //关联的二级分类
    private List<CategoryTwoVo> categoryTwoVos;
}
