package com.controller;

import com.exception.ShiroException;
import com.service.UserService;
import com.utils.JWTUtil;
import com.vo.ResponseVO;
import com.vo.UserVO;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public ResponseVO login(@RequestParam(value = "name") String name,
                            @RequestParam(value = "pass") String pass) {
        UserVO userVO = userService.getUserByName(name);
        if (userVO.getPass().equals(pass)) {
            return ResponseVO.response().setData(JWTUtil.sign(name, pass)).build();
        } else {
            throw new ShiroException("密码错误");
        }
    }

    @GetMapping("/users")
    @RequiresAuthentication
    public ResponseVO users(@RequestParam(value = "pageNum") Integer pageNum,
                            @RequestParam(value = "pageSize") Integer pageSize) {
        return ResponseVO.response().setData(userService.getUserList(pageNum, pageSize)).build();
    }

    @GetMapping
    public ResponseVO userInfo() {
        return null;
    }
}
