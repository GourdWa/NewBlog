package com.hzx.blog.service;

import com.hzx.blog.bean.User;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-14:13
 */
public interface UserService {
    /**
     * 根据用户名和密码查找用户
     * @param username
     * @param password
     * @return
     */
    User checkUser(String username, String password);

    User getUserById(Long id);
}
