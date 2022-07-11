package com.kkb.cubemall.product.vo;

import lombok.Data;

@Data
public class CategoryThreeVo {

    private String id;

    private String name;

    //二级分类id
    private String categoryTwoId;
}
