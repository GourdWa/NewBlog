package com.hzx.blog.service.impl;

import com.hzx.blog.bean.Tag;
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
        //此时需要更新Redis中的标签列表，同时将新添加的标签也放入Redis
        tagRedisTemplate.opsForValue().set("tags", tagMapper.selectList(null), 1, TimeUnit.HOURS);
        tagRedisTemplate.opsForValue().set("tag_" + tag.getId(), tag, 1, TimeUnit.HOURS);
        return tag;
    }

    @Override
    public List<Tag> getTagsByIds(List<Long> idList) {
        List<Tag> tags = tagMapper.selectBatchIds(idList);
        return tags;
    }
}
