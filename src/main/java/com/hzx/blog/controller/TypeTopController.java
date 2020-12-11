package com.hzx.blog.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-04-20:10
 */
@Controller
public class TypeTopController {
    private static final Integer PAGE_SIZE = 6;
    @Autowired
    private TypeService typeService;

    @GetMapping("/types/{id}")
    public String types(@PathVariable Long id, Model model,
                        @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        List<Type> types = typeService.listTop(9999);
        //也就是刚进入分类页面
        if (id == -1) {
            id = types.get(0).getId();
        }
        model.addAttribute("types", types);
        model.addAttribute("activeTypeId", id);
        Page<Blog> blogPage = typeService.getBlogsByIdTop(id, currentNo, PAGE_SIZE);
        model.addAttribute("page", blogPage);
        return "types";
    }
}
