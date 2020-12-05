package com.hzx.blog.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.ArrayList;
import java.util.List;


@TableName("t_type")
public class Type implements Comparable<Type> {
    @TableId(type = IdType.AUTO)
    private Long id;
    //类型名称
    private String name;
    //一个类型对应多个博客
    @TableField(exist = false)
    private List<Blog> blogs = new ArrayList<>();
    //已经发布的博客数量
    @TableField(exist = false)
    private Integer publishedBlogNum = 0;

    public Type() {
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
        return "Type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", blogs=" + blogs +
                ", blogNum=" + publishedBlogNum +
                '}';
    }

    @Override
    public int compareTo(Type o) {
        //按照拥有的博客数量从大到小排序
        return o.publishedBlogNum - this.publishedBlogNum;
    }
}
