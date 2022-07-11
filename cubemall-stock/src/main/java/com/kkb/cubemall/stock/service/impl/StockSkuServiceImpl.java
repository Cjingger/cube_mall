package com.kkb.cubemall.stock.service.impl;

import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.stock.entity.StockOrderTaskDetailEntity;
import com.kkb.cubemall.stock.entity.StockOrderTaskEntity;
import com.kkb.cubemall.stock.service.StockOrderTaskDetailService;
import com.kkb.cubemall.stock.service.StockOrderTaskService;
import com.kkb.cubemall.stock.web.vo.OrderItemVo;
import com.kkb.cubemall.stock.web.vo.SkuHasStockVo;
import com.kkb.cubemall.stock.web.vo.StockSkuLockVo;
import com.kkb.cubemall.stock.web.vo.StockSkuVo;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.Query;

import com.kkb.cubemall.stock.dao.StockSkuDao;
import com.kkb.cubemall.stock.entity.StockSkuEntity;
import com.kkb.cubemall.stock.service.StockSkuService;
import org.thymeleaf.util.StringUtils;


@Service("stockSkuService")
public class StockSkuServiceImpl extends ServiceImpl<StockSkuDao, StockSkuEntity> implements StockSkuService {
    //注入StockOrderTaskService
    private StockOrderTaskService stockOrderTaskService;

    //注入StockSkuDao
    private StockSkuDao stockSkuDao;

    //注入OrderTaskDetailService
    private StockOrderTaskDetailService orderTaskDetailService;
 @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StockSkuEntity> page = this.page(
                new Query<StockSkuEntity>().getPage(params),
                new QueryWrapper<StockSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @Description: 根据skuID查询数据信息
     * @Author: hubin
     * @CreateDate: 2021/6/1 15:37
     * @UpdateUser: hubin
     * @UpdateDate: 2021/6/1 15:37
     * @UpdateRemark: 修改内容
     * @Version: 1.0
     */
    @Override
    public R getSkuStock(List<Long> skuIds) {

        // 创建一个集合，封装对象
        // stream
        List<StockSkuVo> stockSkuList =  skuIds.stream().map(skuId -> {
            // 根据skuID查询库存信息
            StockSkuEntity stockSkuEntity = this.baseMapper.selectOne(new QueryWrapper<StockSkuEntity>().eq("sku_id", skuId));
            // 获取库存数量
            Integer stock = stockSkuEntity.getStock();

            // 创建封装数据对象
            StockSkuVo stockSkuVo = new StockSkuVo();
            stockSkuVo.setSkuId(skuId);
            // 是否有库存
            stockSkuVo.setHasStock(stock == null?false:stock>0);
            return stockSkuVo;
        }).collect(Collectors.toList());

        return R.ok().setData(stockSkuList);
    }

    /**
     * @Description: 调用库存服务,锁定库存
     * @Author: jiingger
     * @CreateDate: 2022/1/3
     * @UpdateUser: jiingger
     * @UpdateDate: 2021/6/1 15:37
     * @UpdateRemark: 修改内容
     * @Version: 1.3
     */
    @Override
    public boolean lockOrderStock(StockSkuLockVo stockSkuLockVo) {

        // 定义保存锁定状态
        boolean skuStockLocked = false;
        //保存订单工作单信息
        StockOrderTaskEntity stockOrderTaskEntity = new StockOrderTaskEntity();
        stockOrderTaskEntity.setOrderId(stockSkuLockVo.getOrderId());
        //保存
        stockOrderTaskService.save(stockOrderTaskEntity);
        //根据收到地址,找到最近的库存仓库,锁定库存
        //找到每个商品属于哪一个库存,且判断库存是否充足,若条件满足,锁定库存
        List<OrderItemVo> lockList = stockSkuLockVo.getLockList();
        List<SkuHasStockVo> skuHasStockVoList = lockList.stream().map(item -> {
             //创建对象
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(item.getSkuId());
            skuHasStockVo.setNum(item.getCount());
            //查询商品在哪个仓库有库存
            //总库存-购买商品数量,若大于0,则可以锁定库存
            List<Long> stockIds = stockSkuDao.selectHasStockListStockIds(item.getSkuId());
            skuHasStockVo.setStockId(stockIds);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        //锁定库存
        for (SkuHasStockVo hasStockVo : skuHasStockVoList){
            List<Long> stockIds = hasStockVo.getStockId();
            //判断仓库是否存在
            if(!StringUtils.isEmpty(String.valueOf(stockIds))){
                //锁定每个商品库存
                for (Long stockId : stockIds){
                    //锁定,成功返回1,否则返回0
                    Long count = stockSkuDao.lockSkuStock(hasStockVo.getSkuId(), hasStockVo.getStockId(), hasStockVo.getNum());
                    if (count == 1){
                        skuStockLocked = true;
                        //锁定成功,保存工单详细信息
                        StockOrderTaskDetailEntity orderTaskDetailEntity = new StockOrderTaskDetailEntity();
                        orderTaskDetailEntity.setSkuId(hasStockVo.getSkuId());
                        orderTaskDetailEntity.setSkuNum(hasStockVo.getNum());
                        orderTaskDetailEntity.setSkuName("");
                        orderTaskDetailEntity.setTaskId(orderTaskDetailEntity.getId());
                        //保存实体
                        orderTaskDetailService.save(orderTaskDetailEntity);
                    }
                }
            }

        }
        return skuStockLocked;
    }

}