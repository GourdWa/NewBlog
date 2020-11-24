package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Type;
import com.hzx.blog.dao.TypeMapper;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-21:04
 */
@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeMapper typeMapper;
    @Autowired
    private BlogService blogService;

    @Autowired
    RedisTemplate typeRedisTemplate;

    @Override
    public Type getById(Long typeId) {
        //同样首先从Redis中获取，如果没有才从数据库中查询
        Type t = (Type) typeRedisTemplate.opsForValue().get("type_" + typeId);
        if (t == null) {
            t = typeMapper.selectById(typeId);
            typeRedisTemplate.opsForValue().set("type_" + typeId, t, 1, TimeUnit.HOURS);
        }
        return t;
    }

    @Override
    public List<Type> list() {
        //结合Redis
        List<Type> types = (List<Type>) typeRedisTemplate.opsForValue().get("types");
        if (types == null) {
            types = typeMapper.selectList(null);
            typeRedisTemplate.opsForValue().set("types", types, 1, TimeUnit.HOURS);
        }
        return types;
    }

    @Override
    public Page<Type> list(int pageNo, int size) {
        //传入当前页和每页显示的条数
        Page<Type> page = new Page<>(pageNo, size);
        Page<Type> typePage = typeMapper.selectPage(page, null);
        return typePage;
    }

    @Override
    public boolean delete(Long typeId) {
        List<Blog> blogs = blogService.getByTypeId(typeId);
        //如果该类型没有关联的博客，则可以删除，否在删除不成功
        if (blogs == null || blogs.size() == 0) {
            //同时要清除redis中的缓存
            typeRedisTemplate.delete("type_" + typeId);
            typeRedisTemplate.delete("types");
            //不等于0则删除成功
            return typeMapper.deleteById(typeId) != 0;
        }
        return false;
    }

    @Override
    public boolean save(String typeName) {
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", typeName);
        Type type = typeMapper.selectOne(queryWrapper);
        //说明没有该类型可以放心添加
        if (type == null) {
            //首先清除Redis中的types
            typeRedisTemplate.delete("types");
            type = new Type();
            type.setName(typeName);
            return typeMapper.insert(type) != 0;
        }
        return false;
    }
}
