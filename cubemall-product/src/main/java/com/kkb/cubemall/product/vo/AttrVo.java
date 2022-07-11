package com.kkb.cubemall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.kkb.cubemall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class AttrVo extends AttrEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 搜索类型
     */
    private Integer searchType;
    /**
     * 图标
     */
    private String icon;
    /**
     * 选择值
     */
    private String valueSelect;
    /**
     * 属性类型:0-销售属性,1-基本属性,2-既是基本属性又是销售属性
     */
    private Integer attrType;
    /**
     * 启用
     */
    private Long enable;
    /**
     * 分类ID
     */
    private Integer categoryId;
    /**
     * 描述
     */
    private Integer showDesc;

    /**
     * 分组ID
     */
    private Long attrGroupId;

}
