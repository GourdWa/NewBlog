package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.blog.bean.User;
import com.hzx.blog.dao.UserMapper;
import com.hzx.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-14:13
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User checkUser(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("password", password);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        return user;
    }
}
