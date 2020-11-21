package com.hzx.blog.service.impl;

import com.hzx.blog.bean.Type;
import com.hzx.blog.dao.TypeMapper;
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
}
