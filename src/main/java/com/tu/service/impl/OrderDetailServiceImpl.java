package com.tu.service.impl;

import com.tu.entity.OrderDetail;
import com.tu.mapper.OrderDetailMapper;
import com.tu.service.IOrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细表 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

}
