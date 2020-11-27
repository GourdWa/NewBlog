package com.hzx.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-26-20:54
 */
@Controller
@RequestMapping("/admin")
public class TagController {
    @Autowired
    private TagService tagService;
    //定义分页时，每页显示的记录数
    private static final Integer PAGE_SIZE = 3;

    @GetMapping("/tags")
    public String list(Model model, @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Tag> tagPage = tagService.list(currentNo, PAGE_SIZE);
        model.addAttribute("page", tagPage);
        return "admin/tags";
    }

    /**
     * 新增页面跳转
     */
    @GetMapping("/tags/input")
    public String input() {
        return "admin/tags_input";
    }

    /**
     * 保存新提交的标签
     */

    @PostMapping("/tags")
    public String save(Tag tag, Model model, RedirectAttributes redirectAttributes) {
        boolean success = tagService.save(tag);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "【" + tag.getName().trim() + "】新增成功");
            return "redirect:/admin/tags";
        } else {
            model.addAttribute("message", "【" + tag.getName().trim() + "】新增失败，请检查是否有重名现象");
            return "admin/tags_input";
        }
    }

    /**
     * 删除标签逻辑
     *
     * @param tagId
     * @return
     */
    @GetMapping("/tags/{tagId}/delete")
    @ResponseBody
    public boolean delete(@PathVariable(name = "tagId") Long tagId) {
        boolean success = tagService.delete(tagId);
        return success;
    }


    /**
     * 内部转发，主要是为了增加删除后的提示信息
     */
    @GetMapping("/tags/delete2list")
    public String delete2list(@RequestParam(value = "deleteName", required = false) String deleteName,
                              @RequestParam(value = "currentNo", required = false) Integer currentNo,
                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "【" + deleteName + "】删除成功");
        return "redirect:/admin/tags?currentNo=" + currentNo;
    }

    /**
     * 编辑标签名称
     */

    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable(value = "id") Long id, Model model) {
        Tag tag = tagService.getById(id);
        model.addAttribute("name", tag.getName());
        model.addAttribute("id", id);
        return "admin/tags_edit";
    }

    @PostMapping("/tags/update")
    public String update(Tag tag, RedirectAttributes redirectAttributes, Model model) {
        boolean success = tagService.update(tag);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "操作成功");
            return "redirect:/admin/tags";
        } else {
            model.addAttribute("message", "名称修改失败，请检查是否有重名现象");
            //回显数据
            model.addAttribute("name", tag.getName());
            model.addAttribute("id", tag.getId());
            return "admin/tags_edit";
        }
    }
}
