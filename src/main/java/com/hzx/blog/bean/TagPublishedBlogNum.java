package com.hzx.blog.bean;

/**
 * @author Zixiang Hu
 * @description 中间类，拿来辅助使用
 * @create 2020-11-30-17:04
 */
public class TagPublishedBlogNum {
    private Long tagId;
    private Integer publishedBlogNum;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Integer getPublishedBlogNum() {
        return publishedBlogNum;
    }

    public void setPublishedBlogNum(Integer publishedBlogNum) {
        this.publishedBlogNum = publishedBlogNum;
    }

    @Override
    public String toString() {
        return "TagPublishedBlogNum{" +
                "tagId=" + tagId +
                ", publishedBlogNum=" + publishedBlogNum +
                '}';
    }
}
