package com.hzx.blog.service;

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
}
