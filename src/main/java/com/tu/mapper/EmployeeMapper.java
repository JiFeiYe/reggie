package com.tu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tu.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 员工信息 Mapper 接口
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-04
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
