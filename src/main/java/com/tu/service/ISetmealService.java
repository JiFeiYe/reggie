package com.tu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tu.dto.SetmealDto;
import com.tu.entity.Setmeal;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
public interface ISetmealService extends IService<Setmeal> {

    void saveWithSetmealDish(SetmealDto setmealDto);

    void updateWithSetmealDish(SetmealDto setmealDto);

    void removeSetmeal(List<Long> ids);
}
