package com.kkb.cubemall.stock.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SkuHasStockVo {
    private Long skuId;

    private Integer num;

    private List<Long> stockId;
}
