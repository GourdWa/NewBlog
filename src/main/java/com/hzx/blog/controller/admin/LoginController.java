package com.hzx.blog.controller.admin;

import com.hzx.blog.bean.User;
import com.hzx.blog.service.UserService;
import com.hzx.blog.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-9:49
 */
@Controller
@RequestMapping("admin")
public class LoginController {
    @Autowired
    UserService userService;

    /**
     * 后台跳转登录页面
     *
     * @return
     */
    @GetMapping
    public String loginPage(HttpSession httpSession) {
        //如果在登录的情况下，也就是Session中存在user的情况下，直接来到首页
        if (httpSession.getAttribute("user") != null) {
            return "admin/index";
        }
        return "admin/login";
    }

    /**
     * 登录逻辑实现
     *
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        HttpSession httpSession,
                        RedirectAttributes redirectAttributes) {
        //如果session中存在用户，直接来到首页，无需再次验证
        if (httpSession.getAttribute("user") != null) {
            return "admin/index";
        }
        User user = userService.checkUser(username, MD5Utils.code(password));
        //说明用户名或者密码错误
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "用户名或者密码错误");
            return "redirect:/admin";
        }
        user.setPassword(null);
        httpSession.setAttribute("user", user);
        return "admin/index";
    }

    /**
     * 注销登录
     *
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return "redirect:/admin";
    }
}
