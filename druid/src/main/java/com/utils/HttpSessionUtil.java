package com.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * http 获取当前请求的session
 */
public class HttpSessionUtil {

    /**
     * 获取
     */
    private static final ServletRequestAttributes servletRequestAttributes;

    /**
     *
     */
    static {
        servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取当前请求的session
     *
     * @return
     */
    public static HttpSession getHttpSession() {
        return servletRequestAttributes.getRequest().getSession();
    }

    /**
     * 获取当前请求的session
     *
     * @return
     */
    public static HttpServletRequest getHttpRequest() {
        return servletRequestAttributes.getRequest();
    }
}
