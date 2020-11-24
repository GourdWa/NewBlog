package com.hzx.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     *
     * @param typeId
     * @return
     */
    Type getById(Long typeId);

    /**
     * 展示全部的类型，无分页
     *
     * @return
     */
    List<Type> list();

    /**
     * 分页展示全部的类型
     */
    Page<Type> list(int pageNo, int size);

    /**
     * 根据id删除类型
     * @param typeId
     * @return
     */
    boolean delete(Long typeId);

    /**
     * 保存新增的博客
     * @param typeName
     * @return
     */
    boolean save(String typeName);
}
