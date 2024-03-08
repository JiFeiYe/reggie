package com.tu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.dto.SetmealDto;
import com.tu.entity.Setmeal;
import com.tu.entity.SetmealDish;
import com.tu.mapper.SetmealMapper;
import com.tu.service.ISetmealDishService;
import com.tu.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    @Transactional
    @Override
    public void saveWithSetmealDish(SetmealDto setmealDto) {

        // 先保存主表
        this.save(setmealDto);

        // 放入
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).toList();

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {

        // 更新主表
        this.updateById(setmealDto);

        // 删除子表
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(lqw);

        // 插入子表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).toList();

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeSetmeal(List<Long> ids) {

        // 先删从表
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        // 再删主表
        this.removeBatchByIds(ids);
    }


}
