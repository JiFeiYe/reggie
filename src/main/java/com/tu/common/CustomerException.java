package com.tu.common;

/**
 * 自定义异常
 *
 * @author JiFeiYe
 * @since 2024/3/6
 */
public class CustomerException extends RuntimeException {

    public CustomerException(String message) {
        super(message);
    }
}
