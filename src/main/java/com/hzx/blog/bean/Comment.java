package com.hzx.blog.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@TableName("t_comment")
public class Comment {
    //评论的id
    @TableId(type = IdType.AUTO)
    private Long id;
    //评论者的昵称
    private String nickname;
    //评论者的邮箱
    private String email;
    //评论的内容
    private String content;
    //评论者的头像
    private String avatar;
    //评论的时间
    private Date createTime;
    //标记是不是管理员的评论
    private boolean adminComment;
    //关联的博客
    private Long blogId;
    @TableField(exist = false)
    private Blog blog;
    //子评论的id集
    @TableField(exist = false)
    private List<Comment> replyCommentIds = new ArrayList<>();
    //父评论
    private Integer parentCommentId;
    @TableField(exist = false)
    private Comment parentComment;

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isAdminComment() {
        return adminComment;
    }

    public void setAdminComment(boolean adminComment) {
        this.adminComment = adminComment;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public List<Comment> getReplyCommentIds() {
        return replyCommentIds;
    }

    public void setReplyCommentIds(List<Comment> replyCommentIds) {
        this.replyCommentIds = replyCommentIds;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", content='" + content + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createTime=" + createTime +
                ", adminComment=" + adminComment +
                ", blogId=" + blogId +
                ", blog=" + blog +
                ", replyCommentIds=" + replyCommentIds +
                ", parentCommentId=" + parentCommentId +
                ", parentComment=" + parentComment +
                '}';
    }
}
