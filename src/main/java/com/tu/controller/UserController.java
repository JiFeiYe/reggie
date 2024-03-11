package com.tu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tu.common.R;
import com.tu.entity.User;
import com.tu.service.IUserService;
import com.tu.utils.SMSUtils;
import com.tu.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author JiFeiYe
 * @since 2024-03-10
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("开始发送验证码");

        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("code: {}", code);
            // 调用阿里云的短信服务API
            SMSUtils.sendMessage("", "", phone, code);
            // 将验证码保存到session
            session.setAttribute(phone, code);

            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map m, HttpSession session) {
        log.info("开始登录验证");

        String phone = (String) m.get("phone");
        String code = (String) m.get("code");

        String codeInSession = (String) session.getAttribute(phone);
        if (codeInSession != null && codeInSession.equals(code)) {
            // 判断手机号是否为新用户
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);
            if (user == null) {
                // 新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
