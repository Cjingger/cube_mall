package com.kkb.cubemall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName SpuAttrGroupVo
 *
 */

@ToString
@Data
public class SpuAttrGroupVo {

    private String groupName;

    //属性参数
    private List<Attr> attrs;
}
