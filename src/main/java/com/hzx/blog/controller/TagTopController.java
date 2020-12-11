package com.hzx.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-04-21:02
 */
@Controller
public class TagTopController {
    private static final Integer PAGE_SIZE = 6;
    @Autowired
    private TagService tagService;

    @GetMapping("/tags/{id}")
    public String types(@PathVariable Long id, Model model,
                        @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        List<Tag> tags = tagService.listTop(9999);
        //也就是刚进入分类页面
        if (id == -1) {
            id = tags.get(0).getId();
        }
        model.addAttribute("tags", tags);
        model.addAttribute("activeTagId", id);
        //查找出该标签类型所有发布的博客
        Page<Blog> blogPage = tagService.getBlogsByIdTop(id, currentNo, PAGE_SIZE);
        model.addAttribute("page", blogPage);
        return "tags";
    }
}
