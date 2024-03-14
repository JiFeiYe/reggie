package com.tu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tu.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单明细表 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}
