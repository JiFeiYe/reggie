package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tu.common.R;
import com.tu.entity.Employee;
import com.tu.service.IEmployeeService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-04
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController extends HttpServlet {

    @Autowired
    private IEmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> Login(@RequestBody Employee employee, HttpServletRequest request, HttpServletResponse response) {
        String password = employee.getPassword();
        password =  DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee e = employeeService.getOne(lqw);

        // Username不存在
        if (e == null) {
            return R.error("用户名不存在！");
        }
        // Password错误
        if (!password.equals(e.getPassword())) {
            return R.error("密码错误！");
        }
        // 账户是否被ban：0禁用，1正常
        if (e.getStatus() == 0) {
            return R.error("账号被封禁！");
        }
        request.setAttribute("employee", e.getId());
        return R.success(e);
    }

    @PostMapping("/logout")
    public R<String> Logout(HttpServletRequest request) {
        request.removeAttribute("employee");
        return R.success("退出成功！");
    }
}
