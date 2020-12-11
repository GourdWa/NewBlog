package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.*;
import com.hzx.blog.dao.TagMapper;
import com.hzx.blog.exception.CommonException;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.BlogTagService;
import com.hzx.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-18-10:21
 */
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogTagService blogTagService;
    @Autowired
    private RedisTemplate tagRedisTemplate;

    @Override
    public List<Tag> list() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags;
    }

    @Transactional
    @Override
    public Tag save(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tagMapper.insert(tag);
        return tag;
    }

    @Transactional
    @Override
    public boolean save(Tag tag) {
        tag.setName(tag.getName().trim());
        Tag tag1 = getByName(tag.getName());
        //说明新增的这个标签还不存在
        if (tag1 == null) {
            //去除两边无意义的空格
            return tagMapper.insert(tag) != 0;
        }
        return false;
    }

    @Override
    public Tag getByName(String tagName) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tagName);
        Tag tag = tagMapper.selectOne(queryWrapper);
        return tag;
    }

    @Override
    public List<Tag> getTagsByIds(List<Long> idList) {
        List<Tag> tags = tagMapper.selectBatchIds(idList);
        return tags;
    }

    @Override
    public Page<Tag> list(Integer currentNo, Integer pageSize) {
        Page<Tag> page = new Page<>(currentNo, pageSize);
        Page<Tag> tagPage = tagMapper.selectPage(page, null);
        return tagPage;
    }

    @Transactional
    @Override
    public boolean delete(Long tagId) {
        List<BlogTag> blogTags = blogTagService.getBlogTagByTagId(tagId);
        //等于null说明没有博客与该标签关联，否在返回false，该标签还要博客关联，无法删除
        if (blogTags == null || blogTags.size() == 0) {
            return tagMapper.deleteById(tagId) != 0;
        }
        return false;
    }

    @Override
    public Tag getById(Long id) {
        Tag t = tagMapper.selectById(id);
        return t;
    }

    @Transactional
    @Override
    public boolean update(Tag newTag) {
        //去除无意义的空格
        newTag.setName(newTag.getName().trim());
        Tag tag = getByName(newTag.getName());
        //说明该标签的名称不会冲突
        if (tag == null) {
            return tagMapper.updateById(newTag) != 0;
        }
        return false;
    }

    /***************************************************前端展示实现***************************************************/

    @Override
    public List<Tag> listTop(Integer size) {
        List<Tag> tags = new ArrayList<>();
        //首先需要查询出全部已经发布的博客的id集合，然后再去映射表blog_tag中查询tag的id，并且这些tag对应的博客必须是发布的
        List<Integer> blogIdList = blogService.getPublishedIdList();
        //获得包含最多发布博客的前size个标签的id
        List<TagPublishedBlogNum> tagPublishedBlogNums = blogTagService.getPublishedTagIds(size, blogIdList);
        for (TagPublishedBlogNum tagPublishedBlogNum : tagPublishedBlogNums) {
            Tag tag = tagMapper.selectById(tagPublishedBlogNum.getTagId());
            tag.setPublishedBlogNum(tagPublishedBlogNum.getPublishedBlogNum());
            tags.add(tag);
        }
        Collections.sort(tags);
        return tags;
    }

    @Override
    public Page<Blog> getBlogsByIdTop(Long id, Integer currentNo, Integer pageSize) {
        List<BlogTag> blogTags = blogTagService.getBlogTagByTagId(id);
        if (blogTags == null || blogTags.size() == 0)
            throw new CommonException("该标签没有关联的博客");
        //获取这些博客的id
        List<Long> blogIds = blogTags.stream().map(blogTag -> blogTag.getBlogId()).collect(Collectors.toList());
        //根据获取的博客id，获取已经发布的博客
        Page<Blog> blogPage = blogService.getBlogsByIdsTop(blogIds, new Page<>(currentNo, pageSize));
        return blogPage;
    }
}
