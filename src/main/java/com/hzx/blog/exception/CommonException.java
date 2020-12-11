package com.hzx.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Zixiang Hu
 * @description 资源未找到异常
 * @create 2020-12-11-15:58
 */
public class CommonException extends RuntimeException {

    public CommonException() {
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
