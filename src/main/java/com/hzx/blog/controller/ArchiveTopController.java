package com.hzx.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Archive;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.service.BlogService;
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
 * @create 2020-12-09-19:40
 */
@Controller
public class ArchiveTopController {
    private static final Integer PAGE_SIZE = 10;
    @Autowired
    private BlogService blogService;

    @GetMapping("/archives/{year}")
    public String list(@PathVariable(value = "year") Integer year,
                       @RequestParam(value = "currentNo", required = false) Integer currentNo
            , Model model) {
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        List<Archive> archives = blogService.getYearList();
        model.addAttribute("archives", archives);
        if (year == -1)
            year = archives.get(0).getYear();
        Page<Blog> blogPage = blogService.getBlogByYear(currentNo, PAGE_SIZE, year);
        model.addAttribute("page", blogPage);
        model.addAttribute("year", year);
        return "archives";
    }

}
