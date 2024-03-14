package com.tu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tu.entity.Orders;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
public interface IOrdersService extends IService<Orders> {

    void submit(Orders orders);
}
