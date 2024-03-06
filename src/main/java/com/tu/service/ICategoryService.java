package com.tu.service;

import com.tu.common.R;
import com.tu.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
public interface ICategoryService extends IService<Category> {

    public R<String> removeCategory();
}
