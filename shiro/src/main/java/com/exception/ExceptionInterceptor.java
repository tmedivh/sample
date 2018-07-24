package com.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C), 2011-2015 温州贷
 * Date: 2018/1/24 14:45
 * Description:
 * Author: miaoyusong@wzdai.com
 */
@RestControllerAdvice
public class ExceptionInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);
    private static final String ERROR_DESC = "服务器内部异常";

    /**
     * 同意异常处理类
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Map<String, Object> handleCustomException(Exception ex) {
        //打印错误异常
        logger.error(ex.getMessage(), ex);
        //判断各异常
        //未知异常
        return resultMap(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * 包装返回信息
     *
     * @param status_code
     * @param message
     * @param data
     * @return
     */
    private Map<String, Object> resultMap(int status_code, String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", status_code);
        result.put("msg", message);
        result.put("data", data);
        return result;
    }
}
