package com.tu.service.impl;

import com.tu.entity.Orders;
import com.tu.mapper.OrdersMapper;
import com.tu.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
