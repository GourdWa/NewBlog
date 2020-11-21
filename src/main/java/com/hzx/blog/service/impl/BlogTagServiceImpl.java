package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.blog.bean.BlogTag;
import com.hzx.blog.dao.BlogTagMapper;
import com.hzx.blog.service.BlogTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-21-16:21
 */
@Service
public class BlogTagServiceImpl implements BlogTagService {
    @Autowired
    private BlogTagMapper blogTagMapper;

    @Override
    public int save(long blogId, long tagId) {
        int insert = blogTagMapper.insert(new BlogTag(blogId, tagId));
        return insert;
    }

    @Override
    public boolean delete(Long blogId) {
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        int delete = blogTagMapper.delete(queryWrapper);
        return delete != 0;
    }

    @Override
    public List<BlogTag>  getTagIdsByBlogId(long blogId) {
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        List<BlogTag> blogTags = blogTagMapper.selectList(queryWrapper);
        return blogTags;
    }
}
