package com.hzx.blog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.TypeService;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-30-15:59
 */
@SpringBootTest
public class ServiceTest {
    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @Test
    public void test01() {
        List<Type> types = typeService.listTop(2);
        System.out.println(types);
    }

    @Test
    public void test02() {
        Page<Blog> blogPage = blogService.getBlogByYear(1, 2, 2020);
        System.out.println(blogPage.getRecords());
    }
}
