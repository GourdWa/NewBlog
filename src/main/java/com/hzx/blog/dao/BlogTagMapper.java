package com.hzx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzx.blog.bean.BlogTag;
import com.hzx.blog.bean.TagPublishedBlogNum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-18-10:16
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {
    /**
     * //获得包含最多发布博客的前size个标签
     * @param size
     * @param blogIdList
     * @return
     */
    List<TagPublishedBlogNum> getPublishedTagIds(@Param("size")Integer size, @Param("list") List<Integer> blogIdList);
}
