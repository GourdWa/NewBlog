package com.hzx.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Type;
import com.hzx.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-22-16:48
 */
@Controller
@RequestMapping("/admin")
public class TypeController {
    //定义分页时，每页显示的记录数
    private static final Integer PAGE_SIZE = 3;
    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    public String list(Model model, @RequestParam(value = "currentNo", required = false) Integer currentNo) {
        if (currentNo == null || currentNo < 1)
            currentNo = 1;
        Page<Type> typePage = typeService.list(currentNo, PAGE_SIZE);
        model.addAttribute("page", typePage);
        return "admin/types";
    }

    @GetMapping("/types/{typeId}/delete")
    @ResponseBody
    public boolean delete(@PathVariable(name = "typeId") Long typeId) {
        boolean success = typeService.delete(typeId);
        return success;
    }

    /**
     * 内部转发，主要是为了增加删除后的提示信息
     */
    @GetMapping("/types/delete2list")
    public String delete2list(@RequestParam(value = "deleteName", required = false) String deleteName,
                              @RequestParam(value = "currentNo", required = false) Integer currentNo,
                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "【" + deleteName + "】删除成功");
        return "redirect:/admin/types?currentNo=" + currentNo;
    }

    /**
     * 后台点击新增按钮，跳转类型新增页面
     *
     * @return
     */
    @GetMapping("/types/input")
    public String input() {
        return "admin/types_input";
    }

    @PostMapping("/types")
    public String save(String name, RedirectAttributes redirectAttributes, Model model) {
        boolean success = typeService.save(name.trim());
        if (success) {
            redirectAttributes.addFlashAttribute("message", "【" + name.trim() + "】新增成功");
            return "redirect:/admin/types";
        } else {
            model.addAttribute("message", "【" + name.trim() + "】新增失败，请检查是否有重名现象");
            return "admin/types_input";
        }
    }
}
