package com.tu.controller;


import com.tu.common.R;
import com.tu.entity.Orders;
import com.tu.service.IOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    /**
     * 订单提交
     *
     * @param orders 订单
     * @return String
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单提交");

        ordersService.submit(orders);
        return R.success("订单提交成功！");
    }
}
