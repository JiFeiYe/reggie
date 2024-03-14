package com.tu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.entity.ShoppingCart;
import com.tu.mapper.ShoppingCartMapper;
import com.tu.service.IShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-11
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

}
