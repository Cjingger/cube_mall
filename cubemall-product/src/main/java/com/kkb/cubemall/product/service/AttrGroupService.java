package com.kkb.cubemall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.product.entity.AttrGroupEntity;
import com.kkb.cubemall.product.vo.AttrGroupWithAttrsVo;
import com.kkb.cubemall.product.vo.SpuAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组表
 *
 * @author
 * @email @gmail.com
 * @date 2021-04-19 18:24:09
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long categoryId);


    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCategoryId(Long categoryId);

    //根据spuId,categoryId查询sku分组规格参数属性值
    List<SpuAttrGroupVo> getGroupAttr(Long spuId, Long categoryId);
}

