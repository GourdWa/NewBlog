package com.hzx.blog.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.ArrayList;
import java.util.List;

@TableName("t_tag")
public class Tag implements Comparable<Tag> {
    @TableId(type = IdType.AUTO)
    private Long id;
    //标签名
    private String name;
    @TableField(exist = false)
    private List<Blog> blogs = new ArrayList<>();
    //已经发布的博客数量
    @TableField(exist = false)
    private Integer publishedBlogNum = 0;

    public Tag() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }

    public Integer getPublishedBlogNum() {
        return publishedBlogNum;
    }

    public void setPublishedBlogNum(Integer publishedBlogNum) {
        this.publishedBlogNum = publishedBlogNum;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", blogs=" + blogs +
                ", publishedBlogNum=" + publishedBlogNum +
                '}';
    }

    @Override
    public int compareTo(Tag o) {
        return o.publishedBlogNum - this.publishedBlogNum;
    }
}
