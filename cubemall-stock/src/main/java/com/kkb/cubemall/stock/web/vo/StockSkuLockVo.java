package com.kkb.cubemall.stock.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName StockSkuLockVo
 * @Description 锁定库存Vo对象
 * @Author 
 * @Date 2021/6/7 16:06
 * @Version V1.0
 **/
@ToString
@Data
public class StockSkuLockVo {

    private Long orderId;

    /*锁定商品库存信息*/
    private List<OrderItemVo> lockList;

    /**
     * @param :
     */
}

