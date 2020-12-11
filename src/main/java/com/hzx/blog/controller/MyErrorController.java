package com.hzx.blog.controller;

import com.hzx.blog.exception.CommonException;
import com.hzx.blog.utils.IpAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/**
 * @author Zixiang Hu
 * @description 主要是为了处理掉空白的异常页面
 * @create 2020-12-11-19:04
 */
@Controller
public class MyErrorController implements ErrorController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping("/error")
    public String myErrorPage(HttpServletRequest request, Model model) {
        String ipAddress = IpAddressUtil.getIpAddress(request);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = 0;
        if (status != null) {
            code = Integer.parseInt(status.toString());
        }
        if (code == 404) {
            logger.error("请求异常，请求的Ip地址为【{}】，异常代码为【{}】", ipAddress, code);
            model.addAttribute("message", "请求地址映射不存在");
        } else {
            logger.error("请求异常，请求的Ip地址为【{}】，异常代码为【{}】", ipAddress, code);
            model.addAttribute("message", "盲僧你发现华点了");
        }
        model.addAttribute("url", null);
        return "error/error";
    }
}
