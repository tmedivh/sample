package com.service;

import com.exception.ShiroException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mapper.UserInfoMapper;
import com.model.UserInfo;
import com.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Copyright (C), 2011-2018 温州贷
 * Author: miaoyusong@wzdai.com
 * Date: 2018-07-06-16:18
 * Description:
 */
@Service
public class UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 根据用户名查询用户
     *
     * @param name
     * @return
     */
    public UserVO getUserByName(String name) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name");
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if (null == userInfos) {
            throw new ShiroException("查不到用户信息");
        }
        UserVO userVO = new UserVO();
        userVO.setName(userInfos.get(0).getName());
        userVO.setPass(userInfos.get(0).getPassword());
        return userVO;
    }

    /**
     * 用户列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<UserInfo> getUserList(Integer pageNum, Integer pageSize) {
        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name");
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> users = userInfoMapper.selectByExample(example);
        return new PageInfo<>(users);
    }
}
