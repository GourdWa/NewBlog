package com.hzx.blog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-10:37
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index(){
        System.out.println("测试AOP");
        return "index";
    }
}
