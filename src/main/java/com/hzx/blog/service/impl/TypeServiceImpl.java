package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Type;
import com.hzx.blog.dao.TypeMapper;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
    public Type getByName(String typeName) {
        QueryWrapper<Type> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", typeName);
        Type type = typeMapper.selectOne(queryWrapper);
        return type;
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

    @Transactional
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

    @Transactional
    @Override
    public boolean save(String typeName) {
        Type type = getByName(typeName);
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

    @Transactional
    @Override
    public boolean update(Type newType) {
        //首先去除两端的空格
        newType.setName(newType.getName().trim());
        Type type = getByName(newType.getName());
        if (type == null) {
            //首先清除Redis中的数据
            typeRedisTemplate.delete("type_" + newType.getId());
            typeRedisTemplate.delete("types");
            return typeMapper.updateById(newType) != 0;
        }
        //如果不等于null说明重复，更新失败
        return false;
    }

    /***************************************************前端展示实现***************************************************/
    @Override
    public List<Type> listTop(Integer size) {
        //获得已发表博客的类型id
        List<Type> types = new ArrayList<>();
        List<Integer> typeIds = blogService.getPublishedTypeIds(size);
        for (Integer typeId : typeIds) {
            Type type = typeMapper.selectById(typeId);
            Integer num = blogService.getBlogNumByPublishedAndTypeId(typeId);
            type.setPublishedBlogNum(num);
            types.add(type);
            Collections.sort(types);
        }
        return types;
    }


    @Override
    public Page<Blog> getBlogsByIdTop(Long id, Integer pageNo, Integer pageSize) {
        Page<Blog> page = new Page<>(pageNo, pageSize);
        //获得该类型已经发表的博客
        Page<Blog> blogPage = blogService.getBlogsByTypeIdTop(id, page);
        return blogPage;
    }
}
