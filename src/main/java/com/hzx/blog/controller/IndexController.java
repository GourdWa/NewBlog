package com.hzx.blog.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.TagService;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-10:37
 */
@Controller
public class IndexController {
    private static final Integer PAGE_SIZE = 3;
    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Blog> blogPage = blogService.listTop(currentNo, PAGE_SIZE);
        //展示出包含博客最多的前n个类型和标签
        List<Type> types = typeService.listTop(6);
        List<Tag> tags = tagService.listTop(8);
        List<Blog> recommendBlogs = blogService.listRecommendBlogTop(3);
        //获得分页的数据
        model.addAttribute("page", blogPage);
        //列举出博客引用数最多的前几个类型
        model.addAttribute("types", types);
        model.addAttribute("tags", tags);
        model.addAttribute("recommendBlogs", recommendBlogs);
        return "index";
    }
    /*
    打开博客详情页
    */
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable(name = "id") Long id, Model model) {
        Blog blog = blogService.getAndConvert(id);
        //后台查验，是否该博客已经发布
        if (blog.isPublished())
            //需要将博客的内容转换为html
            model.addAttribute("blog", blogService.getAndConvert(id));
        return "blog";
    }

    /**
     * 博客查询，主要是查询博客标题和内容
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "currentNo", required = false) Integer currentNo,
                         Model model, @RequestParam(name = "query") String query) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Blog> blogPage = blogService.listBlogTop(currentNo, PAGE_SIZE, query);
        //前端展示的查询
        model.addAttribute("page", blogPage);
        model.addAttribute("query", query);
        return "search";
    }

    /*
     * 填充底部的最新推荐
     */
    @GetMapping("/footer/newblog")
    public String newBlogs(Model model) {
        model.addAttribute("newBlogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newbloglist";
    }
}
