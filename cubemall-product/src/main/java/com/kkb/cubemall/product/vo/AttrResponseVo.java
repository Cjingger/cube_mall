package com.kkb.cubemall.product.vo;

import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo {

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分组名称
     */
    private String attrGroupName;

    /**
     * 分类完整路径
     */
    private Long[] categoryPath;
}
