package com.controller;

import com.exception.ShiroException;
import com.service.UserService;
import com.shiro.JWTToken;
import com.utils.JWTUtil;
import com.vo.ResponseVO;
import com.vo.UserVO;
import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.apache.shiro.SecurityUtils.getSubject;

/**
 * Copyright (C), 2011-2018 温州贷
 * Author: miayusong@wzdai.com
 * Date: 2018-04-17-17:17
 * Description:
 */
@RestController
@RequestMapping("/shiro")
@Api(description = "用户")
public class SampleController {
    @Autowired
    private UserService userService;

    private static final String AUTHORIZATION = "token";

    @PostMapping("/login")
    public ResponseVO login(@RequestParam(value = "name") String name,
                            @RequestParam(value = "pass") String pass) {
        UserVO userVO = userService.getUserByName(name);
        if (userVO.getPass().equals(pass)) {
            Session session = getSubject().getSession(true);
            String jwt = JWTUtil.sign(name, session.getId().toString(), pass);
            session.setAttribute(AUTHORIZATION, jwt);
            return ResponseVO.response().setData(jwt).build();
        } else {
            throw new ShiroException("密码错误");
        }
    }

    @GetMapping("/users")
    @RequiresAuthentication
    public ResponseVO users(@RequestParam(value = "pageNum") Integer pageNum,
                            @RequestParam(value = "pageSize") Integer pageSize) {
        Subject subject = getSubject();
        boolean isLogin = subject.isAuthenticated();
        return ResponseVO.response().setData(userService.getUserList(pageNum, pageSize)).build();
    }

    @GetMapping
    public ResponseVO userInfo() {
        return null;
    }
}
