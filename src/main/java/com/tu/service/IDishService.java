package com.tu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tu.dto.DishDto;
import com.tu.entity.Dish;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
public interface IDishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);
}
