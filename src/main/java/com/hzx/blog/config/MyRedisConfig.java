package com.hzx.blog.config;

import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.net.UnknownHostException;
import java.time.Duration;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-16-20:39
 */
@Configuration
public class MyRedisConfig {
    /*
    操作Type的Redis
     */
    @Bean
    public RedisTemplate<Object, Type> typeRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Type> template = new RedisTemplate<>();
        //指定序列化机制，不使用默认的序列化
        GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setDefaultSerializer(redisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /*
     操作Tag的Redis
     */
    @Bean
    public RedisTemplate<Object, Tag> tagRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Tag> template = new RedisTemplate<>();
        //指定序列化机制，不使用默认的序列化
        GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setDefaultSerializer(redisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /*
    操作Blog的Redis
     */
    @Bean
    public RedisTemplate<Object, Blog> blogRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Blog> template = new RedisTemplate<>();
        //指定序列化机制，不使用默认的序列化
        GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setDefaultSerializer(redisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public RedisCacheManager employeeRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofDays(1))   // 设置缓存过期时间为一天
                        .disableCachingNullValues()     // 禁用缓存空值，不缓存null校验
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new
                                GenericJackson2JsonRedisSerializer()));     // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration).build();     // 设置默认的cache组件
        return cacheManager;
    }
}
