package com.controller;

import com.annotation.Log;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mapper.UserInfoMapper;
import com.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright (C), 2011-2018 温州贷
 * Author: miaoyusong@wzdai.com
 * Date: 2018-04-17-17:17
 * Description:
 */
@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @GetMapping
    @Log
    public Object test() {
        PageHelper.startPage(1, 15);
        throw new RuntimeException("12312");
        //return new PageInfo<>(userInfoMapper.selectAll());
    }

    @PutMapping
    @Transactional
    public Object update() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setName("miao");
        return userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }
}
