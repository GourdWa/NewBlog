package com.hzx.blog;

import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.BlogTag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.bean.User;
import com.hzx.blog.dao.BlogMapper;
import com.hzx.blog.dao.BlogTagMapper;
import com.hzx.blog.dao.TypeMapper;
import com.hzx.blog.dao.UserMapper;
import com.hzx.blog.utils.EmailUtil;
import com.hzx.blog.utils.MD5Utils;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class BlogApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    BlogMapper blogMapper;

    @Autowired
    RedisTemplate typeRedisTemplate;

    @Autowired
    TypeMapper typeMapper;

    @Autowired
    RedisTemplate blogRedisTemplate;

    @Autowired
    BlogTagMapper blogTagMapper;

    @Test
    void contextLoads() {
        String title = "博客";
        for (int i = 0; i < 10; i++) {
            Blog blog = new Blog();
            blog.setTitle(title + i);
            blog.setAppreciation(i % 2 == 0);
            blog.setCommentabled(i % 3 == 1);
            blog.setContent(UUID.randomUUID().toString());
            blog.setDescription(UUID.randomUUID().toString());
            blog.setFirstPicture(UUID.randomUUID().toString());
            blog.setFlag("原创");
            blog.setGoodJob(0);
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setUserId(1L);
            blog.setTypeId((long) new Random().nextInt(3));
            blogMapper.insert(blog);
        }
    }

    @Test
    void test01() {

        long mysqlStart = System.currentTimeMillis();
        List<Blog> blogs = blogMapper.selectList(null);
        System.out.println("Mysql耗时：" + (System.currentTimeMillis() - mysqlStart));

        long redisStart = System.currentTimeMillis();
        List<Blog> blogs1 = (List<Blog>) blogRedisTemplate.opsForValue().get("blogs");
        System.out.println("redis耗时：" + (System.currentTimeMillis() - redisStart));
    }

    @Test
    public void test02() {
        BlogTag blogTag = new BlogTag();
        blogTag.setBlogId(1L);
        blogTag.setTagId(1L);
        int i = blogTagMapper.insert(blogTag);

    }

    @Test
    public void test03() {
        List<Integer> typeIds = blogMapper.getPublishedTypeIds(2);
        System.out.println(typeIds);
    }

    @Test
    public void test04() {
        Integer num = blogMapper.getBlogNumByPublishedAndTypeId(2);
        System.out.println(num);
    }

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @Test
    public void emailTest01() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("Java邮件测试");
        simpleMailMessage.setText("from new_blog");
        simpleMailMessage.setTo("huzixiang_uestc@163.com");
        simpleMailMessage.setFrom("1312685188@qq.com");
        javaMailSender.send(simpleMailMessage);
    }

    @Test
    public void emailTest02() {
//        EmailUtil.sendEmail("huzixiang_uestc@163.com", "开题阅读文献", "http://47.110.126.140/blog/54");
        new EmailUtil().sendEmail("huzixiang_uestc@163.com","测试");
    }
}
