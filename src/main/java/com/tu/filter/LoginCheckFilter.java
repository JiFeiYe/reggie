package com.tu.filter;

import com.alibaba.fastjson.JSON;
import com.tu.common.BaseContext;
import com.tu.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author JiFeiYe
 * @since 2024/3/4
 */
@WebFilter(urlPatterns = "/*") // 拦截所有资源
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("开始拦截资源");
        String url = request.getRequestURI();
        log.info("拦截到：{}", url);
        // 设置白名单
        String[] whiteList = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg", // 发送短信
                "/user/login", // 手机端登录
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        if (check(whiteList, url)) {
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("白名单放行：{}", url);
            return;
        }

        // todo: bug 后端登录后将导致前端未登录也能进入?
        // 前端登录验证
        Object user = request.getSession().getAttribute("user");
        if (user != null) {
            // 若已经登陆（session有信息），放行
            log.info("将user id：{} 放入线程", user);
            BaseContext.setCurrentId((Long) user);

            filterChain.doFilter(servletRequest, servletResponse);
            log.info("已经登录，放行");
            return;
        }

        // 后端登录验证
        Object employee = request.getSession().getAttribute("employee");
        if (employee != null) {
            // 若已经登陆（session有信息），放行
            log.info("将employee id：{} 放入线程", employee);
            BaseContext.setCurrentId((Long) employee);

            filterChain.doFilter(servletRequest, servletResponse);
            log.info("已经登录，放行");
            return;
        }

        log.info("未登录，无法放行：{}", url);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls, String url) {
        for (String u : urls) {
            boolean match = PATH_MATCHER.match(u, url);
            if (match) {
                log.info("检测到：{}", u);
                return true;
            }
        }
        return false;
    }
}
