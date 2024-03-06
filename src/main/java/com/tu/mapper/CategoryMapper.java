package com.tu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tu.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品及套餐分类 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {


}
