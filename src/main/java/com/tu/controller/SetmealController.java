package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.R;
import com.tu.dto.SetmealDto;
import com.tu.entity.Category;
import com.tu.entity.Setmeal;
import com.tu.entity.SetmealDish;
import com.tu.service.ICategoryService;
import com.tu.service.ISetmealDishService;
import com.tu.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-06
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;

    @Autowired
    private ISetmealDishService setmealDishService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 套餐分页查询
     *
     * @param page     当前页面
     * @param pageSize 页面大小
     * @param name     模糊查询
     * @return IPage
     */
    @GetMapping("/page")
    public R<IPage<SetmealDto>> getSetmealPage(Integer page, Integer pageSize, String name) {
        log.info("套餐分页查询");

        // 查询主表
        IPage<Setmeal> setmealIPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealIPage, lqw);

        // 补充父表信息
        IPage<SetmealDto> setmealDtoIPage = new Page<>();
        BeanUtils.copyProperties(setmealIPage, setmealDtoIPage, "records");
        List<Setmeal> records = setmealIPage.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).toList();

        setmealDtoIPage.setRecords(list);
        return R.success(setmealDtoIPage);
    }

    /**
     * 套餐多表新增
     *
     * @param setmealDto Setmeal+SetmealDish
     * @return SetmealDto
     */
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info("套餐多表新增 {}", setmealDto);

        setmealService.saveWithSetmealDish(setmealDto);
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("保存成功！");
    }

    /**
     * 修改套餐查询
     *
     * @param id 套餐id
     * @return SetmealDto
     */
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable Long id) {
        log.info("修改套餐查询 id: {}", id);

        Setmeal setmeal = setmealService.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询从表
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);

        setmealDto.setSetmealDishes(setmealDishes);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     *
     * @param setmealDto Setmeal+SetmealDish
     * @return String
     */
    @PutMapping
    public R<String> editSetmeal(@RequestBody SetmealDto setmealDto) {
        log.info("修改套餐 setmealDto: {}", setmealDto);

        setmealService.updateWithSetmealDish(setmealDto);
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改成功！");
    }

    /**
     * 多表删除套餐
     *
     * @param ids SetmealIds
     * @return String
     */
    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids) {
        log.info("多表删除套餐 ids: {}", ids);

        // 删除缓存
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = setmealService.list(setmealLambdaQueryWrapper);
        for (Setmeal setmeal : setmeals) {
            Long categoryId = setmeal.getCategoryId();
            String key = "setmeal_" + categoryId + "_1";
            log.info("key:{}", key);
            redisTemplate.delete(key);
        }

        setmealService.removeSetmeal(ids);
        return R.success("删除成功！");
    }

    /**
     * 单独/批量停售
     *
     * @param ids SetmealIds
     * @return String
     */
    @PostMapping("/status/0")
    public R<String> setmealStatusByStatus1(@RequestParam List<Long> ids) {
        log.info("单独/批量停售 SetmealIds: {}", ids);

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId, ids);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(0);
        setmealService.update(setmeal, lqw);
        return R.success("停售成功！");
    }

    /**
     * 单独/批量启售
     *
     * @param ids SetmealIds
     * @return String
     */
    @PostMapping("/status/1")
    public R<String> setmealStatusByStatus2(HttpServletRequest request, @RequestParam List<Long> ids) {
        log.info("单独/批量启售 SetmealIds: {}", ids);

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId, ids);
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(1);
        setmealService.update(setmeal, lqw);
        return R.success("启售成功！");
    }

    /**
     * 获取菜品分类对应的套餐
     *
     * @param categoryId 分类id
     * @param status     状态
     * @return SetmealDto
     */
    @GetMapping("/list")
    public R<List<Setmeal>> setmealList(Long categoryId, Integer status) {
        log.info("开始获取菜品分类对应的套餐");

        String key = "setmeal_" + categoryId + "_1";
        List<Setmeal> setmeals = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        if (setmeals != null) {
            return R.success(setmeals);
        }

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Setmeal::getCategoryId, categoryId)
                .eq(Setmeal::getStatus, status)
                .orderByDesc(Setmeal::getUpdateTime);
        setmeals = setmealService.list(lqw);
        redisTemplate.opsForValue().set(key, setmeals, 30, TimeUnit.MINUTES);
        return R.success(setmeals);
    }

}
