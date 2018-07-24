package com.shiro;

import com.alibaba.fastjson.JSON;
import com.utils.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("token");
        return authorization != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("token");
        if (null == authorization) {
            return false;
        }
        String sessionId = JWTUtil.getSessionId(authorization);
        if (sessionId == null) {
            throw new AuthenticationException("token invalid");
        }
        /**
         * 获取token中的sessionId
         * 通过sessionId获取session对象
         * 如果根据sessionId获取不到session对象,表示用户未登陆,或token失效
         */
        SessionKey key = new WebSessionKey(sessionId, request, response);
        Session session = SecurityUtils.getSecurityManager().getSession(key);
        if (session == null) {
            return false;
        } else {
            /**
             * 认证token。这样就可以使用@RequiresAuthentication或者subject.isAuthenticated()
             */
            getSubject(request, response).login(new JWTToken(authorization));
        }
        return true;
    }

    /**
     * 这里我们详细说明下为什么最终返回的都是true，即允许访问
     * 例如我们提供一个地址 GET /article
     * 登入用户和游客看到的内容是不同的
     * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
     * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                responseWrit(response);
            }
        }
        return true;
    }

    private void responseWrit(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", HttpStatus.UNAUTHORIZED.value());
        resp.put("msg", HttpStatus.UNAUTHORIZED);
        resp.put("data", "");
        try {
            out = response.getWriter();
            out.append(JSON.toJSONString(resp));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
