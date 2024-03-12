package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tu.common.BaseContext;
import com.tu.common.R;
import com.tu.entity.ShoppingCart;
import com.tu.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 保存到购物车
     *
     * @param shoppingCart 购物车
     * @return ShoppingCart
     */
    @PostMapping("/add")
    public R<ShoppingCart> addCart(@RequestBody ShoppingCart shoppingCart) {
        log.info("开始保存到购物车 shoppingCart:{}", shoppingCart);

        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 填充数据并查询购物车是否空
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(lqw);

        // 当前物品购物车空的
        if (shoppingCart1 == null) {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        } else {
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
        }
        return R.success(shoppingCart1);
    }

    /**
     * 获取购物车列表
     *
     * @return ShoppingCarts
     */
    @GetMapping("list")
    public R<List<ShoppingCart>> cartList() {
        log.info("获取购物车列表");

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId)
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);
        return R.success(shoppingCarts);
    }

    /**
     * 购物车某一商品数量减一
     *
     * @param map dishId or setmealId
     * @return String
     */
    @PostMapping("/sub")
    public R<String> subCart(@RequestBody Map map) {
        log.info("购物车某一商品数量减一 map:{}", map);

        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<ShoppingCart> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        if (map.get("dishId") != null) {
            lqw.eq(ShoppingCart::getDishId, map.get("dishId"));
        } else {
            lqw.eq(ShoppingCart::getSetmealId,map.get("setmealId"));
        }
        ShoppingCart shoppingCart = shoppingCartService.getOne(lqw);
        if (shoppingCart.getNumber() > 1) {
            lqw.setSql("number = number - 1");
            shoppingCartService.update(lqw);
            return R.success("减一成功！");
        } else if (shoppingCart.getNumber() == 1) {
            shoppingCartService.removeById(shoppingCart);
            return R.success("减一成功！");
        }
        return R.success("减一失败！");
        // todo
    }

    /**
     * 清空购物车
     * @return String
     */
    @DeleteMapping("clean")
    public R<String> clearCart() {
        log.info("开始清空购物车");

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(lqw);
        return R.success("清空购物车成功！");
    }
}
