package com.hzx.blog.service;

import com.hzx.blog.bean.Type;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-21:03
 */
public interface TypeService {
    /**
     * 根据类型id获取类型实例
     * @param typeId
     * @return
     */
    Type getById(Long typeId);

     List<Type> list();
}
