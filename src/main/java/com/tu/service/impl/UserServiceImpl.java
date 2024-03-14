package com.tu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tu.entity.User;
import com.tu.mapper.UserMapper;
import com.tu.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
