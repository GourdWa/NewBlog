package com.hzx.blog.exception;

import com.hzx.blog.utils.IpAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-11-16:01
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 拦截全部异常
     *
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, HttpServletRequest request) throws Exception {
        String ipAddress = IpAddressUtil.getIpAddress(request);
        StringBuffer url = request.getRequestURL();
        logger.error("请求异常，请求的Ip地址为【{}】，请求URL为【{}】，异常信息为【{}】", ipAddress, url, ex.getMessage());
        model.addAttribute("ipAddress", ipAddress);
        model.addAttribute("url", url);
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }
}
