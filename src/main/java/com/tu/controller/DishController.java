package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.R;
import com.tu.dto.DishDto;
import com.tu.entity.Category;
import com.tu.entity.Dish;
import com.tu.entity.DishFlavor;
import com.tu.service.ICategoryService;
import com.tu.service.IDishFlavorService;
import com.tu.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 菜品管理 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private IDishService dishService;

    @Autowired
    private IDishFlavorService dishFlavorService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 菜品多表新增
     *
     * @param dishDto Dish+DishFlavor
     * @return String
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto) {
        log.info("菜品多表新增 dishDto: {}", dishDto);

        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("保存成功");
    }

    /**
     * 菜品多表查询分页
     *
     * @param page     当前页面
     * @param pageSize 页面大小
     * @param name     模糊查询
     * @return IPage
     */
    @GetMapping("/page")
    public R<IPage<DishDto>> getDishPage(Integer page, Integer pageSize, String name) {
        log.info("菜品多表查询分页");

        // 获取主表分页信息
        IPage<Dish> dishIPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime);
        dishService.page(dishIPage);

        // 将主表分页信息拷贝到DishDto中
        IPage<DishDto> dishDtoIPage = new Page<>();
        BeanUtils.copyProperties(dishIPage, dishDtoIPage, "Records");
        // 遍历主表records， 往DishDto中补充categoryName
        List<Dish> records = dishIPage.getRecords();
        List<DishDto> dishDtos = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            // 查询出categoryName，若非空则将其放进dishDto
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).toList();

        dishDtoIPage.setRecords(dishDtos);
        return R.success(dishDtoIPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDish(@PathVariable Long id) {
        log.info("菜品多表查询单id");

        // 先查主表
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 再查从表
        LambdaQueryWrapper<DishFlavor> dishDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishDtoLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishDtoLambdaQueryWrapper);
        if (dishFlavors != null) {
            dishDto.setFlavors(dishFlavors);
        }

        return R.success(dishDto);
    }

    /**
     * 多表修改
     *
     * @param dishDto 主从混合表
     * @return String
     */
    @PutMapping
    public R<String> editDish(@RequestBody DishDto dishDto) {
        log.info("菜品多表修改dish：{}", dishDto);

        dishService.updateWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改成功！");
    }

    /**
     * 多表删除
     *
     * @param ids DishId列表
     * @return String
     */
    @Transactional
    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids) {
        log.info("多表删除 DishIds:{}", ids);

        // 删除缓存
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, ids);
        List<Dish> dishes = dishService.list(dishLambdaQueryWrapper);
        for (Dish dish : dishes) {
            Long categoryId = dish.getCategoryId();
            String key = "dish_" + categoryId + "_1";
            log.info("key:{}",key);
            redisTemplate.delete(key);
        }

        // 先删从表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        // 再删主表
        dishService.removeBatchByIds(ids);

        return R.success("删除成功");
    }

    /**
     * 单独/批量停售
     *
     * @param ids DishId列表
     * @return String
     */
    @PostMapping("/status/0")
    public R<String> dishStatusByStatus1(@RequestParam List<Long> ids) {
        log.info("单独/批量停售 DishIds:{}", ids);

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        Dish dish = new Dish();
        dish.setStatus(0);
        dishService.update(dish, lqw);
        return R.success("停售成功！");
    }

    /**
     * 单独/批量启售
     *
     * @param ids DishId列表
     * @return String
     */
    @PostMapping("/status/1")
    public R<String> dishStatusByStatus2(@RequestParam List<Long> ids) {
        log.info("单独/批量启售 DishIds:{}", ids);

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.in(Dish::getId, ids);
        Dish dish = new Dish();
        dish.setStatus(1);
        dishService.update(dish, lqw);
        return R.success("启售成功！");
    }

    /**
     * 菜品分类查询对应的菜品数据
     *
     * @param categoryId 套餐ID
     * @return List-Dish
     */
    @GetMapping("/list")
    public R<List<DishDto>> queryDishList(Long categoryId) {
        log.info("菜品分类查询对应的菜品数据 categoryId: {}", categoryId);

        String key = "dish_" + categoryId + "_1";
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null) {
            return R.success(dishDtos);
        }

        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1);
        List<Dish> dishes = dishService.list(lqw);

        dishDtos = dishes.stream().map((dish) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Category category = categoryService.getById(dish.getCategoryId());
            dishDto.setCategoryName(category.getName());

            LambdaQueryWrapper<DishFlavor> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(DishFlavor::getDishId, dish.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqw1);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).toList();

        redisTemplate.opsForValue().set(key, dishDtos, 30, TimeUnit.MINUTES);
        return R.success(dishDtos);
    }
}
