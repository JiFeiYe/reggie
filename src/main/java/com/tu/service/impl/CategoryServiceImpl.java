package com.tu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.common.CustomerException;
import com.tu.entity.Category;
import com.tu.entity.Dish;
import com.tu.entity.Setmeal;
import com.tu.mapper.CategoryMapper;
import com.tu.service.ICategoryService;
import com.tu.service.IDishService;
import com.tu.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Autowired
    private IDishService dishService;

    @Autowired
    private ISetmealService setmealService;

    @Override
    public void removeCategory(Long id) {

        log.info("检查子表dish连接状态");
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            throw new CustomerException("该分类下仍有菜品，无法删除！");
        }

        log.info("检查子表setmeal连接状态");
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomerException("该分类下仍有套餐，无法删除！");
        }

        super.removeById(id);
    }
}
