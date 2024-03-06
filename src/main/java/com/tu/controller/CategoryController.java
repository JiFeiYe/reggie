package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.BaseContext;
import com.tu.common.R;
import com.tu.entity.Category;
import com.tu.service.ICategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 菜品及套餐分类的分页查询
     *
     * @param page     当前页数
     * @param pageSize 页面大小
     * @return IPage
     */
    @GetMapping("/page")
    public R<IPage<Category>> getCategoryPage(Integer page, Integer pageSize) {
        log.info("菜品以及套餐分类的分页查询, page:{}, pageSize:{}", page, pageSize);

        IPage<Category> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        p = categoryService.page(p, lqw);
        return R.success(p);
    }

    /**
     * 新增菜品及套餐分类
     *
     * @param category
     * @return R
     */
    @PostMapping
    public R<String> addCategory(HttpServletRequest request, @RequestBody Category category) {
        log.info("新增菜品及套餐分类：{}", category);

        Object o = request.getSession().getAttribute("employee");
        if (o != null) {
            log.info("将id：{} 放入线程", o);
            BaseContext.setCurrentId((Long) o);
        }

        categoryService.save(category);
        return R.success("保存成功");
    }

    /**
     * 删除分类
     *
     * @param id 待删除分类id
     * @return R
     */
    @DeleteMapping
    public R<String> deleteCategory(Long id) {
        log.info("开始删除分类：{}", id);

        categoryService.removeCategory(id);
        return R.success("删除成功！");
    }

    /**
     * 修改分类信息
     *
     * @param category
     * @return R
     */
    @PutMapping
    public R<String> editCategory(HttpServletRequest request, @RequestBody Category category) {
        log.info("修改分类信息：{}", category);

        Object o = request.getSession().getAttribute("employee");
        if (o != null) {
            log.info("将id：{} 放入线程", o);
            BaseContext.setCurrentId((Long) o);
        }

        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    /**
     * 获取菜品分类列表
     *
     * @param type
     * @return R
     */
    @GetMapping("list")
    public R<List<Category>> getCategoryList(Integer type) {
        log.info("获取菜品分类列表，type: {}", type);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getType, type).orderByDesc(Category::getUpdateTime);
        List<Category> lts = categoryService.list(lqw);
        return R.success(lts);
    }
}
