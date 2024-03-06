package com.tu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tu.entity.Category;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
public interface ICategoryService extends IService<Category> {

    void removeCategory(Long id);
}
