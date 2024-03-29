package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tu.common.R;
import com.tu.entity.Employee;
import com.tu.service.IEmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;


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
@Api(tags = "职工相关api")
public class EmployeeController extends HttpServlet {

    @Autowired
    private IEmployeeService employeeService;

    /**
     * 登入
     *
     * @param employee 职工类
     * @param request  request
     * @return R
     */
    @PostMapping("/login")
    @ApiOperation("登入验证")
    public R<Employee> Login(@ApiParam(name = "emplyee", value = "用户名密码") @RequestBody Employee employee, HttpServletRequest request) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

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
        request.getSession().setAttribute("employee", e.getId());

        return R.success(e);
    }

    /**
     * 登出
     *
     * @param request request
     * @return R
     */
    @PostMapping("/logout")
    public R<String> Logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增职工
     *
     * @return R
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        log.info("新增用户：{}", employee.toString());
        // 设置初始密码并加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 填充新增时间、修改时间、新增操作者、修改操作者
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        // 自动填充
        // 事关MyMetaObjectHandler
        // 测试两个类是否属于同个线程
//        Long id = Thread.currentThread().getId();
//        log.info("servlet类：{}", id);
//        Object o = request.getSession().getAttribute("employee");
//        if (o != null) {
//            log.info("将id：{} 放入线程", o);
//            BaseContext.setCurrentId((Long) o);
//        }

        // 执行新增操作
        log.info("执行新增操作：{}", employee);
        boolean save = employeeService.save(employee);
        if (save) {
            return R.success("新增员工成功");
        }
        return R.error("新增失败");
    }

    /**
     * 分页查询
     *
     * @param page     当前页数
     * @param pageSize 页面大小
     * @param name     查询名字关键字
     * @return IPage
     */
    @GetMapping("/page")
    public R<IPage<Employee>> getByPage(Integer page, Integer pageSize, String name) {
        log.info("分页查询");
        IPage<Employee> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // 根据传进姓名模糊查询（非空判断）
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 根据最后修改时间排序
        lqw.orderByDesc(Employee::getUpdateTime);
        p = employeeService.page(p, lqw);
        return R.success(p);
    }

    /**
     * 员工信息修改/状态更新
     *
     * @param employee 职工类
     * @return String
     */
    @PutMapping
    public R<String> editEmployee(@RequestBody Employee employee) {
        log.info("员工信息修改/状态更新");

        employeeService.updateById(employee);
        return R.success("员工状态更新成功");

        // todo: 更新完员工状态后分页查询不应自动回到第一页
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee e = employeeService.getById(id);
        if (e != null) {
            return R.success(e);
        }
        return R.error("未查询到员工");
    }


}
