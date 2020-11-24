package com.hzx.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-19:56
 */
public interface BlogService {
    /**
     * 无条件分页展示所有博客
     *
     * @param size
     * @return
     */
    Page<Blog> list(int pageNo, int size);

    /**
     * 根据搜索条件展示博客
     *
     * @param pageNo
     * @param size
     * @param title
     * @param typeId
     * @param recommend
     * @return
     */
    Page<Blog> list(int pageNo, int size, String title, Integer typeId, boolean recommend);


    /**
     * 将新增的博客添加到数据库
     */
    Blog save(Blog blog);

    /**
     * 根据博客id删除博客
     */
    boolean delete(Long blogId);

    /**
     * 根据博客id获取博客的信息
     * @param blogId
     * @return
     */
    Blog getBlogById(Long blogId);

    /**
     * 更新博客
     * @param id
     * @param blog
     * @return
     */
    Blog updateBlog(Long id, Blog blog);

    /**
     * 根据类型id获取博客
     * @param typeId
     * @return
     */
    List<Blog> getByTypeId(Long typeId);
}
