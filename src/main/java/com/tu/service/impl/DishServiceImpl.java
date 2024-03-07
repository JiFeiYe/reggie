package com.tu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.dto.DishDto;
import com.tu.entity.Dish;
import com.tu.entity.DishFlavor;
import com.tu.mapper.DishMapper;
import com.tu.service.IDishFlavorService;
import com.tu.service.IDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Autowired
    private IDishFlavorService dishFlavorService;

    /**
     * 保存Dish，连带保存DishFlavor
     * Dish : DishFlavor = 1 : m （一对多表）
     *
     * @param dishDto 主从混合表
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 先保存主表记录（多余无关列不影响）
        this.save(dishDto);

        // 再保存从表记录
        // 先从主表查询id放到从表中
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 遍历flavors
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).toList(); // todo: .collect(Collectors.toList()) 与 .toList() ?
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 修改Dish与DishFlavor
     *
     * @param dishDto 主从混合表
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 先更新主表
        this.updateById(dishDto);

        // 再更新从表（删除对应字段，重新插入新字段）
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).toList();

        dishFlavorService.saveBatch(flavors);
    }


}
