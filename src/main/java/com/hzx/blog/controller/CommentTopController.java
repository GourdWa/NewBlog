package com.hzx.blog.controller;

import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Comment;
import com.hzx.blog.bean.User;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.CommentService;
import com.hzx.blog.utils.EmailUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-08-17:00
 */
@Controller
public class CommentTopController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;
    @Autowired
    Executor asyncExecutor;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        List<Comment> comments = commentService.listCommentByBlogId(blogId);
        model.addAttribute("comments", comments);
        return "blog :: commentList";
    }

    /**
     * 保存评论逻辑
     */
    @PostMapping("/comments")
    public String post(Comment comment, @RequestParam(value = "url", required = false) String url, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        Long blogId = comment.getBlog().getId();
        comment.setBlogId(blogId);
//        comment.setBlog(blogService.getBlogById(comment.getBlog().getId()));
        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        } else {
            comment.setAvatar(avatar);
            comment.setAdminComment(false);
        }
        commentService.saveComment(comment);
        // 2020.12.18 增加邮箱通知留言功能
        Blog blog = blogService.getBlogById(blogId);
        // 2022.03.02 异步留言
        commentService.asyncSendEmail(comment, blog.getTitle(), url);
        return "redirect:/comments/" + comment.getBlog().getId();
    }
}
