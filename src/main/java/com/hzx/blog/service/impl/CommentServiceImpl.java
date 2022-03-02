package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Comment;
import com.hzx.blog.dao.CommentMapper;
import com.hzx.blog.service.BlogService;
import com.hzx.blog.service.CommentService;
import com.hzx.blog.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-08-17:01
 */
@Service
public class CommentServiceImpl implements CommentService {
    private String receiveEmail = "1312685188@qq.com";
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogService blogService;



    List<Comment> tempList = new ArrayList<>();
    /**
     * 评论递归设置子评论
     *
     * @param comments
     * @return
     */
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
        comment.setCreateTime(new Date());
        int insert = commentMapper.insert(comment);
        return insert != 0;
    }

    @Override
    public Page<Comment> list(Integer currentNo, Integer pageSize) {
        Page<Comment> page = new Page<>(currentNo, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        commentPage.getRecords().forEach(
                comment -> comment.setBlog(blogService.getBlogById(comment.getBlogId()))
        );
        return page;
    }

    @Override
    @Transactional
    public Page<Comment> list(Integer pageNo, Integer pageSize, String title, String content) {
        Page<Comment> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (content != null && content.trim().length() > 0)
            queryWrapper.like("content", content.trim());
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        //为这些评论设置关联博客
        commentPage.getRecords().forEach(
                comment -> comment.setBlog(blogService.getBlogById(comment.getBlogId()))
        );
        List<Comment> records = commentPage.getRecords();
        //如果搜索条件还包含博客的标题，则筛选出符合条件的标题
        if (title != null && title.trim().length() > 0) {
            String aTitle = title.trim();
            List<Comment> list = records.stream().filter(comment -> comment.getBlog().getTitle().contains(aTitle)).collect(Collectors.toList());
            commentPage.setRecords(list);
        }
        return commentPage;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).or().eq("parent_comment_id", id);
        commentMapper.delete(queryWrapper);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public Integer deleteByBlogId(Long blogId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);
        return commentMapper.delete(queryWrapper);
    }

    @Override
    @Async("asyncExecutor")
    public void asyncSendEmail(Comment comment, String blogTitle, String url) {
        boolean isReply = false;
        Long parentId = comment.getParentComment().getId();
        if (parentId != -1) {
            isReply = true;//代表这是一个回复留言，不是新留言
            //如果是新留言，那么需要通知其父留言
            Comment parentComment = getCommentById(parentId);
            receiveEmail = parentComment.getEmail();
        }
        EmailUtil.sendEmail(receiveEmail, blogTitle, url, isReply);
    }
}
