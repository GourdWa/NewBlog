package com.hzx.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Comment;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-08-17:01
 */
public interface CommentService {
    /*
    * 根据博客id查询评论信息
    * */
    List<Comment> listCommentByBlogId(Long blogId);

    /**
     * 保存评论
     * @param comment
     */
    boolean saveComment(Comment comment);

    /**
     * 后台管理，展示所有的评论
     * @param currentNo
     * @param pageSize
     * @return
     */
    Page<Comment> list(Integer currentNo, Integer pageSize);

    /**
     * 后端根据评论内容和博客标题搜索
     * @param pageNo
     * @param pageSize
     * @param title
     * @param content
     * @return
     */
    Page<Comment> list(Integer pageNo, Integer pageSize, String title, String content);

    /**
     * 删除指定评论和回复它的评论
     * @param id
     */
    void delete(Long id);

    /**
     * 根据评论id获取评论
     * @param id
     * @return
     */
    Comment getCommentById(Long id);

    /**
     * 根据博客id删除该博客下的评论
     * @param blogId
     * @return
     */
    Integer deleteByBlogId(Long blogId);
}
