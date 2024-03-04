package com.tu.service.impl;

import com.tu.entity.Employee;
import com.tu.mapper.EmployeeMapper;
import com.tu.service.IEmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-04
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

}
