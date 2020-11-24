package com.hzx.blog.interceptor;

import com.hzx.blog.bean.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-9:56
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    /**
     * 目标方法运行前拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("user");
        if (user == null) {
            // 这里可以使用重定向，不过那样就无法携带数据
            request.setAttribute("message","没有权限，请先登录");
            request.getRequestDispatcher("/admin").forward(request, response);

            // 如果没有登录就重定向到登录页面
//            response.sendRedirect("/admin");
            return false;
        }
        return true;
    }
}
