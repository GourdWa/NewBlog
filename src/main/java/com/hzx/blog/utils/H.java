package com.hzx.blog.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-10:58
 */
public class H extends HashMap<String,Object> {
    private static final long serialVersionUID = 1L;

    public H() {
        put("code", 0);
    }

    public static H error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static H error(String msg) {
        return error(500, msg);
    }

    public static H error(int code, String msg) {
        H r = new H();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static H ok(String msg) {
        H r = new H();
        r.put("msg", msg);
        return r;
    }

    public static H ok(Map<String, Object> map) {
        H r = new H();
        r.putAll(map);
        return r;
    }

    public static H ok() {
        return new H();
    }

    public H put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
