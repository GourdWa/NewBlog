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
     *
     * @param blogId
     * @return
     */
    Blog getBlogById(Long blogId);

    /**
     * 更新博客
     *
     * @param id
     * @param blog
     * @return
     */
    Blog updateBlog(Long id, Blog blog);

    /**
     * 根据类型id获取博客
     *
     * @param typeId
     * @return
     */
    List<Blog> getByTypeId(Long typeId);

    /**
     * 前台展示的博客，只列举出已经发布的博客
     *
     * @param currentNo
     * @param pageSize
     * @return
     */
    Page<Blog> listTop(Integer currentNo, Integer pageSize);

    /**
     * 前台展示推荐的博客，根据跟新时间只展示前size个
     *
     * @param size
     * @return
     */
    List<Blog> listRecommendBlogTop(int size);

    /**
     * 前台展示的Blog获取
     */
    Blog getAndConvert(Long id);

    /**
     * 查询博客，返回标题或文本中包含query的博客
     *
     * @param currentNo
     * @param pageSize
     * @param query
     * @return
     */
    Page<Blog> listBlogTop(Integer currentNo, Integer pageSize, String query);

    /**
     * 无重复的返回已发布博客的类型id列表
     *
     * @param size
     * @return
     */
    List<Integer> getPublishedTypeIds(int size);

    /**
     * 获得typeId类型中已经发布的博客数量
     */
    Integer getBlogNumByPublishedAndTypeId(int typeId);

    /**
     * typeId类型中已经发布的博客
     *
     * @param typeId
     * @param page
     * @return
     */
    Page<Blog> getBlogsByTypeIdTop(Long typeId, Page<Blog> page);

    /**
     * 获得已经发布的博客的id列表
     */
    List<Integer> getPublishedIdList();


    /**
     * 根据博客id，获取已经发布的博客，返回分页数据
     *
     * @param blogIds
     * @return
     */
    Page<Blog> getBlogsByIdsTop(List<Long> blogIds, Page<Blog> page);

    /**
     * 更新博客的点赞数
     * @param blog
     */
    boolean updateGoodJob(Blog blog);

}
