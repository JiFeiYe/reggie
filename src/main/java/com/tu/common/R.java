package com.tu.common;

import lombok.Data;

/**
 * @author JiFeiYe
 * @since 2024/3/4
 */
@Data
public class R<T> {
    // 编码：1成功，其他失败
    private Integer code;
    private String msg;
    private T data;

    // 构造成功信息
    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.code = 1;
        r.data = object;
        return r;
    }

    // 构造失败信息
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }
}
