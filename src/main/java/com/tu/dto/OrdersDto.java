package com.tu.dto;

import com.tu.entity.OrderDetail;
import com.tu.entity.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JiFeiYe
 * @since 2024/3/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails = new ArrayList<>();

    private String userNamee;
}
