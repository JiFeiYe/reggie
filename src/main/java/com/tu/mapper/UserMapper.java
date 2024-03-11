package com.tu.mapper;

import com.tu.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-10
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
