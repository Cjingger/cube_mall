package com.kkb.cubemall.product.vo;


import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName: AttrValueAndSkuIdVo
 *
 */
@ToString
@Data
public class AttrValueAndSkuIdVo {

    //sku组合id
    private Integer skuIds;

    // 属性：白色, 128G
    private String attrValue;


}
