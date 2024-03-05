package com.tu.common;

/**
 * @author JiFeiYe
 * @since 2024/3/5
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 从当前线程中获取当前登录员工id
     *
     * @return 登录员工id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 往当前线程中写入当前登录员工id
     *
     * @param id 登录员工id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
}
