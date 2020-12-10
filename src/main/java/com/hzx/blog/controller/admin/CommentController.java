package com.hzx.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Comment;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-09-11:27
 */
@Controller
@RequestMapping("/admin")
public class CommentController {
    //定义分页时，每页显示的记录数
    private static final Integer PAGE_SIZE = 6;
    @Autowired
    private CommentService commentService;

    @GetMapping("/comments")
    public String list(Model model,
                       @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Comment> commentPage = commentService.list(currentNo, PAGE_SIZE);
        model.addAttribute("page", commentPage);
        return "admin/comments";
    }


    @PostMapping("/comments/search")
    public String search(@RequestParam(value = "pageNo", required = false) Integer pageNo,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content,
                         Model model) {
        Page<Comment> commentPage = commentService.list(pageNo, PAGE_SIZE, title, content);
        model.addAttribute("page", commentPage);
        //返回admin/blogs中的blogList片段,实现局部渲染
        return "admin/comments :: commentList";
    }

    /**
     * 删除该评论和它的子评论
     *
     * @param commentId
     */
    @GetMapping("/comments/{commentId}/delete")
    @ResponseBody
    public void delete(@PathVariable(name = "commentId") Long commentId) {
        commentService.delete(commentId);
    }

    @GetMapping("/comments/delete2list")
    public String deleteToListPage(@RequestParam(value = "deleteName", required = true) String deleteName,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "【" + deleteName + "及其子评论】删除成功");
        return "redirect:/admin/comments";
    }

}
