package com.tu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tu.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
