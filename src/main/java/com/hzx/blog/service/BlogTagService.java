package com.hzx.blog.service;


import com.hzx.blog.bean.BlogTag;
import com.hzx.blog.bean.TagPublishedBlogNum;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-21-16:20
 */
public interface BlogTagService {

    /**
     * 保存一条博客和标签的关联
     */

    int save(long blogId, long tagId);

    /**
     * 根据博客id删除关联的标签表（标签并没有删除）
     *
     * @param blogId
     * @return
     */
    boolean delete(Long blogId);

    /**
     * 根据博客id获取关联的tag id
     */
    List<BlogTag> getTagIdsByBlogId(long blogId);

    /**
     * 根据标签id查找博客和标签的映射关系
     *
     * @param tagId
     * @return
     */
    List<BlogTag> getBlogTagByTagId(Long tagId);

    /**
     * 获得包含最多发布博客的前size个标签的id
     */
    List<TagPublishedBlogNum> getPublishedTagIds(int size, List<Integer> blogIdList);
}
