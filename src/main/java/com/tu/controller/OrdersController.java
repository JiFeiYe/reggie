package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.BaseContext;
import com.tu.common.R;
import com.tu.dto.OrdersDto;
import com.tu.entity.OrderDetail;
import com.tu.entity.Orders;
import com.tu.service.IOrderDetailService;
import com.tu.service.IOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private IOrderDetailService orderDetailService;

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

    /**
     * 分页获取订单列表
     * @param page 当前页面
     * @param pageSize 页面大小
     * @return IPage
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> orderPaging(Integer page, Integer pageSize) {
        log.info("开始分页获取订单列表 page:{}, pageSize:{}", page, pageSize);

        IPage<Orders> ordersIPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId())
                .orderByAsc(Orders::getStatus)
                .orderByDesc(Orders::getCheckoutTime);
        ordersService.page(ordersIPage, ordersLambdaQueryWrapper);

        IPage<OrdersDto> ordersDtoIPage = new Page<>();
        BeanUtils.copyProperties(ordersIPage, ordersDtoIPage, "records");
        List<Orders> orders = ordersIPage.getRecords();
        List<OrdersDto> OrdersDto = orders.stream().map((order) -> {
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, order.getId());
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order, ordersDto);
            ordersDto.setOrderDetails(orderDetailService.list(orderDetailLambdaQueryWrapper));
            return ordersDto;
        }).toList();
        ordersDtoIPage.setRecords(OrdersDto);

        log.info("ordersIPage: {}", ordersIPage.getRecords());
        log.info("ordersDtoIPage: {}", ordersDtoIPage.getRecords());
        return R.success(ordersDtoIPage);
    }
}
