package com.kkb.cubemall.product.vo;


import lombok.Data;
import lombok.ToString;
import java.util.List;

/**
 *
 */
@ToString
@Data
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    //属性值
    private List<AttrValueAndSkuIdVo> attrValues;
}
