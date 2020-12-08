package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzx.blog.bean.Comment;
import com.hzx.blog.dao.CommentMapper;
import com.hzx.blog.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-08-17:01
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 评论递归设置子评论
     *
     * @param comments
     * @return
     */

    List<Comment> tempList = new ArrayList<>();

    private List<Comment> eachComment(List<Comment> comments) {
        for (Comment comment : comments) {
            //查出这些评论的子评论
        /*    QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_comment_id", comment.getId());
            List<Comment> sonComments = commentMapper.selectList(queryWrapper);
            sonComments.forEach(c -> c.setParentCommentNickName(comment.getNickname()));
            eachComment(sonComments);
            comment.setReplyComments(sonComments);*/
            recursively(comment);
            comment.setReplyComments(new ArrayList<>(tempList));
            tempList.clear();
        }
        return comments;
    }

    private void recursively(Comment comment) {
        //查出这些评论的子评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_comment_id", comment.getId());
        List<Comment> sonComments = commentMapper.selectList(queryWrapper);
        if (sonComments.size() > 0) {
            sonComments.forEach(c -> c.setParentCommentNickName(comment.getNickname()));
            for (Comment sonComment : sonComments) {
                tempList.add(sonComment);
                recursively(sonComment);

            }
        }
    }

    @Override
    @Transactional
    public List<Comment> listCommentByBlogId(Long blogId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId).eq("parent_comment_id", -1);
        //为评论设置子评论，如果有
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        return eachComment(commentList);
    }


    @Override
    @Transactional
    public boolean saveComment(Comment comment) {
        comment.setParentCommentId(comment.getParentComment().getId());
//        Long parentCommentId = comment.getParentComment().getId();
        //说明有父评论
        /*if (parentCommentId != -1) {
            comment.setParentComment(commentMapper.getOne(parentCommentId));
        } else {
            comment.setParentComment(null);
        }*/
        comment.setCreateTime(new Date());
        int insert = commentMapper.insert(comment);
        return insert != 0;
    }
}
