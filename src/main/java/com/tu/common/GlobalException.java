package com.tu.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 *
 * @author JiFeiYe
 * @since 2024/3/5
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalException {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        log.error(message);
        // 个性化异常提示
        if (message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            return R.error(split[2] + "已存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException ex) {
        String message = ex.getMessage();
        log.error(message);
        return R.error(message);
    }

}
