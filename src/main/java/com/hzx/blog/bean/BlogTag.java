package com.hzx.blog.bean;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author Zixiang Hu
 * @description 用作标签和博客之间多对多的映射中转
 * @create 2020-11-18-10:13
 */
@TableName("t_blog_tag")
public class BlogTag {
    private Long blogId;
    private Long tagId;

    public BlogTag() {
    }

    public BlogTag(Long blogId, Long tagId) {
        this.blogId = blogId;
        this.tagId = tagId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public String toString() {
        return "BlogTag{" +
                "blogId=" + blogId +
                ", tagId=" + tagId +
                '}';
    }
}
