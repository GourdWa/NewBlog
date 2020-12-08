package com.hzx.blog.controller;

import com.hzx.blog.bean.Comment;
import com.hzx.blog.bean.User;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    public String post(Comment comment, HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("user");
        comment.setBlogId(comment.getBlog().getId());
//        comment.setBlog(blogService.getBlogById(comment.getBlog().getId()));
        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        } else {
            comment.setAvatar(avatar);
            comment.setAdminComment(false);
        }

        commentService.saveComment(comment);
        return "redirect:/comments/" + comment.getBlog().getId();
    }
}
