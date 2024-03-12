package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.BaseContext;
import com.tu.common.R;
import com.tu.dto.OrdersDto;
import com.tu.entity.OrderDetail;
import com.tu.entity.Orders;
import com.tu.entity.User;
import com.tu.service.IOrderDetailService;
import com.tu.service.IOrdersService;
import com.tu.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private IUserService userService;

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
     * 分页获取PC端订单列表
     *
     * @param page     当前页面
     * @param pageSize 页面大小
     * @return IPage
     */
    @GetMapping("/page")
    public R<IPage<Orders>> orderPaging(Integer page, Integer pageSize, Long number) {
        log.info("开始分页获取pc端订单列表");

        IPage<Orders> ordersIPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper
                .like(number != null, Orders::getId, number)
                .orderByAsc(Orders::getStatus);
        ordersService.page(ordersIPage, ordersLambdaQueryWrapper);

//        IPage<OrdersDto> ordersDtoIPage = new Page<>();
//        BeanUtils.copyProperties(ordersIPage, ordersDtoIPage, "records");
//
//        List<Orders> orders = ordersIPage.getRecords();
//        List<OrdersDto> ordersDtos = orders.stream().map((order) -> {
//            OrdersDto ordersDto = new OrdersDto();
//            User user = userService.getById(order.getUserId());
//            if (user != null) {
//                ordersDto.setUserNamee(user.getName());
//            }
//            return ordersDto;
//        }).toList();
//
//        ordersDtoIPage.setRecords(ordersDtos);
        return R.success(ordersIPage);
    }


    /**
     * 分页获取移动端订单列表
     *
     * @param page     当前页面
     * @param pageSize 页面大小
     * @return IPage
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> userOrderPaging(Integer page, Integer pageSize) {
        log.info("开始分页获取移动端订单列表 page:{}, pageSize:{}", page, pageSize);

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

    /**
     * PC端订单状态修改
     *
     * @param orders 订单表
     * @return String
     */
    @PutMapping
    public R<String> editOrderDetail(@RequestBody Orders orders) {
        log.info("开始修改订单状态");

        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper
                .eq(Orders::getId, orders.getId())
                .set(Orders::getStatus, orders.getStatus());
        ordersService.update(ordersLambdaUpdateWrapper);
        return R.success("修改成功！");
    }
}
