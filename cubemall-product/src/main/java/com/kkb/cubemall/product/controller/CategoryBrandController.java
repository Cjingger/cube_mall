package com.kkb.cubemall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kkb.cubemall.common.utils.PageUtils;
import com.kkb.cubemall.common.utils.R;
import com.kkb.cubemall.product.entity.BrandEntity;
import com.kkb.cubemall.product.service.BrandService;
import com.kkb.cubemall.product.service.CategoryService;
import com.kkb.cubemall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kkb.cubemall.product.entity.CategoryBrandEntity;
import com.kkb.cubemall.product.service.CategoryBrandService;


/**
 * 分类品牌关系表
 *
 * @author peige
 * @email peige@gmail.com
 * @date 2021-04-22 11:03:03
 */
@RestController
@RequestMapping("product/categorybrand")
public class CategoryBrandController {
    @Autowired
    private CategoryBrandService categoryBrandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService BrandService;

    /**
     * 获取分类关联的品牌信息
     */
    @GetMapping("brands/list")
    public R relationBrandList(@RequestParam Integer categoryId){
         List<BrandEntity> brandEntities = categoryBrandService.getBrandByCategoryId(categoryId);
         brandEntities.stream().map(
                 brandEntity -> {
                     BrandVo brandVo = new BrandVo();
                     brandVo.setBrandId(brandEntity.getId());
                     brandVo.setBrandName(brandEntity.getName());
                     return brandVo;
                 }
         ).collect(Collectors.toList());

         return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("category/list")
    //@RequiresPermissions("product:categorybrand:list")
    public R list(@RequestParam("brandId") Long brandId){
        List<CategoryBrandEntity> data = categoryBrandService.list(
                new QueryWrapper<CategoryBrandEntity>().eq("brand_id", brandId)
        );
        data.forEach(CategoryBrandEntity-> {
            CategoryBrandEntity.setCategoryName(categoryService.getById(CategoryBrandEntity.getCategoryId()).getName());
            CategoryBrandEntity.setBrandName(BrandService.getById(CategoryBrandEntity.getBrandId()).getName());
        });
        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrand:info")
    public R info(@PathVariable("id") Integer id){
		CategoryBrandEntity categoryBrand = categoryBrandService.getById(id);

        return R.ok().put("categoryBrand", categoryBrand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:categorybrand:save")
    public R save(@RequestBody CategoryBrandEntity categoryBrand){
		categoryBrandService.save(categoryBrand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrand:update")
    public R update(@RequestBody CategoryBrandEntity categoryBrand){
		categoryBrandService.updateById(categoryBrand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrand:delete")
    public R delete(@RequestBody CategoryBrandEntity categoryBrandEntity){
		categoryBrandService.remove(new QueryWrapper<CategoryBrandEntity>().eq("brand_id", categoryBrandEntity.getBrandId()));

        return R.ok();
    }

}
