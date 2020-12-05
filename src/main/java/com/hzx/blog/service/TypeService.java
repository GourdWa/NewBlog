package com.hzx.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
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
     * 根据类型名称获取实例
     * @param typeName
     * @return
     */
    Type getByName(String typeName);

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

    /**
     * 更新博客
     * @param newType
     * @return
     */
    boolean update(Type newType);

    /**
     * 用于首页右边栏的展示,找出包含博客数量最多的前size个类型
     * @return
     */
    List<Type> listTop(Integer size);


    /**
     * 根据类型id查询出所有已经发布的博客，并封装成分页返回
     * @param id
     * @return
     */
    Page<Blog> getBlogsByIdTop(Long id, Integer pageNo, Integer pageSize);
}
