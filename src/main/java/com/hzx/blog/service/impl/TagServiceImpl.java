package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.BlogTag;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.dao.BlogTagMapper;
import com.hzx.blog.dao.TagMapper;
import com.hzx.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private BlogTagMapper blogTagMapper;
    @Autowired
    private RedisTemplate tagRedisTemplate;

    @Override
    public List<Tag> list() {
        List<Tag> tags = (List<Tag>) tagRedisTemplate.opsForValue().get("tags");
        //说明Redis中没有缓存，此时从数据库中查询,并将结果缓存进Redis
        if (tags == null) {
            tags = tagMapper.selectList(null);
            tagRedisTemplate.opsForValue().set("tags", tags, 1, TimeUnit.HOURS);
        }
        return tags;
    }

    @Override
    public Tag save(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tagMapper.insert(tag);
        //因为新增了标签，所以将老标签集合删除
        tagRedisTemplate.delete("tags");
        tagRedisTemplate.opsForValue().set("tag_" + tag.getId(), tag);
        return tag;
    }

    @Override
    public boolean save(Tag tag) {
        tag.setName(tag.getName().trim());
        Tag tag1 = getByName(tag.getName());
        //说明新增的这个标签还不存在
        if (tag1 == null) {
            tagRedisTemplate.delete("tags");
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

    @Override
    public boolean delete(Long tagId) {
        QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_id", tagId);
        List<BlogTag> blogTags = blogTagMapper.selectList(queryWrapper);
        //等于null说明没有博客与该标签关联，否在返回false，该标签还要博客关联，无法删除
        if (blogTags == null || blogTags.size() == 0) {
            //清除缓存中的标签列表
            tagRedisTemplate.delete("tags");
            tagRedisTemplate.delete("tag_" + tagId);
            return tagMapper.deleteById(tagId) != 0;
        }
        return false;
    }

    @Override
    public Tag getById(Long id) {
        //同样首先从Redis中获取，如果没有才从数据库中查询
        Tag t = (Tag) tagRedisTemplate.opsForValue().get("tag_" + id);
        if (t == null) {
            t = tagMapper.selectById(id);
            tagRedisTemplate.opsForValue().set("tag_" + id, t, 1, TimeUnit.HOURS);
        }
        return t;
    }

    @Override
    public boolean update(Tag newTag) {
        //去除无意义的空格
        newTag.setName(newTag.getName().trim());
        Tag tag = getByName(newTag.getName());
        //说明该标签的名称不会冲突
        if (tag == null) {
            //清除缓存中的标签列表
            tagRedisTemplate.delete("tags");
            tagRedisTemplate.delete("tag_" + newTag.getId());
            return tagMapper.updateById(newTag) != 0;
        }
        return false;
    }
}
