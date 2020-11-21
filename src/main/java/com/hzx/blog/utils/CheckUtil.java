package com.hzx.blog.utils;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-10-16-20:19
 */
public class CheckUtil {
    /**
     * 检查传递过来的字符串是否全部是数字
     *
     * @param id
     * @return
     */
    public static boolean checkNum(String id) {
        for (int i = 0; i < id.length(); i++) {
            if (!Character.isDigit(id.charAt(i)))
                return false;
        }
        return true;
    }
}
