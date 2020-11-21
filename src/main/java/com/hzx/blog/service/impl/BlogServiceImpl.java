package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.*;
import com.hzx.blog.dao.BlogMapper;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.BlogTagService;
import com.hzx.blog.service.TagService;
import com.hzx.blog.service.TypeService;
import com.hzx.blog.utils.CheckUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-20:03
 */
@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogTagService blogTagService;


    /**
     * 首先根据条件查询出分页数据，并配合Redis为博客的类型赋值
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    public Page<Blog> setTypeByRedis(Page<Blog> page, QueryWrapper<Blog> queryWrapper) {
        Page<Blog> blogPage = blogMapper.selectPage(page, queryWrapper);
        List<Blog> records = blogPage.getRecords();
        //依次为查询到的Blog的type赋值
        for (Blog record : records) {
            Type t = typeService.getById(record.getTypeId());
            record.setType(t);
        }
        return blogPage;
    }


    @Override
    public Page<Blog> list(int pageNo, int size) {
        Page<Blog> page = new Page<>(pageNo, size);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //根据更新时间排序
        queryWrapper.orderByDesc("update_time");
        return setTypeByRedis(page, queryWrapper);
    }


    @Override
    public Page<Blog> list(int pageNo, int size, String title, Integer typeId, boolean recommend) {
        Page<Blog> page = new Page<>(pageNo, size);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //查询前先去除名称两边的空格
        if (title != null && title.trim().length() > 0)
            queryWrapper.like("title", title.trim());
        if (typeId != null)
            queryWrapper.eq("type_id", typeId);
        if (recommend)
            queryWrapper.eq("recommend", true);
        //根据更新时间排序
        queryWrapper.orderByDesc("update_time");
        return setTypeByRedis(page, queryWrapper);
    }

    @Override
    public Blog save(Blog blog) {
        //设置创建时间和更新时间
        Date date = new Date();
        blog.setCreateTime(date);
        blog.setUpdateTime(date);
        //设置点赞数和浏览数
        blog.setGoodJob(0);
        blog.setViews(0);
        //将博客插入
        blogMapper.insert(blog);
        //建立博客和标签的映射
        String tagIds = blog.getTagIds();
        createBlogTagMap(tagIds, blog.getId());
        return blog;
    }

    @Override
    public boolean delete(Long blogId) {
        int deleteA = blogMapper.deleteById(blogId);
        if (deleteA > 0) {
            //说明博客删除成功
            //接下来删除与博客关联的标签（并不是删除标签本身，只是删除二者的关系）
            return blogTagService.delete(blogId);
        }
        return false;
    }

    @Override
    public Blog getBlogById(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        //对查询出来的博客tags和type进行赋值
        Type type = typeService.getById(blog.getTypeId());
        blog.setType(type);
        //封装tags
        List<BlogTag> blogTags = blogTagService.getTagIdsByBlogId(blogId);
        List<Long> idList = new ArrayList<>();
        StringBuilder tagIds = new StringBuilder();
        for (BlogTag blogTag : blogTags) {
            idList.add(blogTag.getTagId());
            tagIds.append(blogTag.getTagId()).append(",");
        }
        List<Tag> tags = tagService.getTagsByIds(idList);
        blog.setTags(tags);
        blog.setTagIds(tagIds.substring(0, tagIds.length() - 1));
        return blog;
    }

    @Override
    public Blog updateBlog(Long id, Blog blog) {
        //先获取出数据库中的博客
        Blog blog1 = blogMapper.selectById(id);
        blog.setCreateTime(blog1.getCreateTime());
        blog.setUpdateTime(new Date());
        blog.setGoodJob(blog1.getGoodJob());
        blog.setViews(blog1.getViews());
        //因为可能更改了标签，因此将以前的标签关联全部删除，并重新插入,,,
        blogTagService.delete(blog1.getId());
        //建立博客和标签的映射
        String tagIds = blog.getTagIds();
        createBlogTagMap(tagIds, blog.getId());
//        BeanUtils.copyProperties(blog, blog1);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", blog1.getId());
        blogMapper.update(blog, queryWrapper);
        return blog1;
    }

    public void createBlogTagMap(String tagIds, long blogId) {
        String[] ids = tagIds.split(",");
        for (String tagId : ids) {
            //如果不是数字，说明这个标签是新插入的，需要将这个标签保存进数据库并更新Redis
            if (!CheckUtil.checkNum(tagId.trim())) {
                Tag saveTag = tagService.save(tagId);
                blogTagService.save(blogId, saveTag.getId());
            } else {
                //如果是数字，说明这个标签之前存在，直接查询即可
                long newId = Long.parseLong(tagId);
                blogTagService.save(blogId, newId);
            }
        }
    }
}
