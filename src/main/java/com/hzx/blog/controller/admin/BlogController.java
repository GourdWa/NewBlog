package com.hzx.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Blog;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.bean.User;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.TagService;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-19:46
 */
@Controller
@RequestMapping("/admin")
public class BlogController {
    //定义分页时，每页显示的记录数
    private static final Integer PAGE_SIZE = 6;
    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String list(Model model,
                       @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        //规范化页数
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Blog> blogPage = blogService.list(currentNo, PAGE_SIZE);
        List<Type> types = typeService.list();
        model.addAttribute("page", blogPage);
        model.addAttribute("types", types);
        return "admin/blogs";
    }


    @PostMapping("/blogs/search")
    public String search(@RequestParam(value = "pageNo", required = false) Integer pageNo,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "typeId", required = false) Integer typeId,
                         @RequestParam(value = "recommend", required = false) boolean recommend,
                         Model model) {
        Page<Blog> blogPage = blogService.list(pageNo, PAGE_SIZE, title, typeId, recommend);
        model.addAttribute("page", blogPage);
        //返回admin/blogs中的blogList片段,实现局部渲染
        return "admin/blogs :: blogList";
    }

    /**
     * 后台点击新增按钮，跳转博客新增页面
     *
     * @return
     */
    @GetMapping("/blogs/input")
    public String input(Model model) {
        model.addAttribute("tags", tagService.list());
        model.addAttribute("types", typeService.list());
        return "admin/blogs_input";
    }

    /**
     * 保存新提交的Blog
     */
    @PostMapping("/blogs")
    public String publish(Blog blog,
                          HttpSession httpSession,
                          RedirectAttributes redirectAttributes) {
        User user = (User) httpSession.getAttribute("user");
        blog.setUser(user);
        blog.setUserId(user.getId());
        Blog saveBlog = blogService.save(blog);
        if (saveBlog == null) {
            redirectAttributes.addFlashAttribute("message", "新增失败");
        } else {
            redirectAttributes.addFlashAttribute("message", "新增成功");
        }
        return "redirect:/admin/blogs";
    }

    /**
     * 删除博客逻辑
     */
    @GetMapping("/blogs/{blogId}/delete")
    @ResponseBody
    public void delete(@PathVariable(name = "blogId") Long blogId) {
        blogService.delete(blogId);
    }

    /**
     * 主要是增加删除成功后的message信息
     *
     * @param deleteName
     * @param currentNo
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/blogs/delete2list")
    public String deleteToListPage(@RequestParam(value = "deleteName", required = false) String deleteName,
                                   @RequestParam(value = "currentNo", required = false) Integer currentNo,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "【" + deleteName + "】删除成功");
        return "redirect:/admin/blogs?currentNo=" + currentNo;
    }


    /**
     * 查询出博客信息后来到博客编辑页面
     */
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable(name = "id") Long id, Model model) {
        Blog blog = blogService.getBlogById(id);
        List<Tag> tags = tagService.list();
        List<Type> types = typeService.list();
        model.addAttribute("blog", blog);
        model.addAttribute("tags", tags);
        model.addAttribute("types", types);
        return "admin/blogs_edit";
    }

    /**
     * 更新博客
     */

    @PostMapping("/blogs/update")
    public String update(Blog blog,
                         RedirectAttributes redirectAttributes,
                         HttpSession httpSession) {
        //设置用户
        User user = (User) httpSession.getAttribute("user");
        blog.setUser(user);
        blog.setUserId(user.getId());
        Blog updateBlog = blogService.updateBlog(blog.getId(), blog);
        if (updateBlog == null) {
            redirectAttributes.addFlashAttribute("message", "修改失败");
        } else {
            redirectAttributes.addFlashAttribute("message", "修改成功");
        }
        return "redirect:/admin/blogs";
    }
}
