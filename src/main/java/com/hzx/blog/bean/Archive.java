package com.hzx.blog.bean;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-09-20:02
 */
public class Archive {
    private Integer year;
    private Integer blogNum;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getBlogNum() {
        return blogNum;
    }

    public void setBlogNum(Integer blogNum) {
        this.blogNum = blogNum;
    }

    @Override
    public String toString() {
        return "Archive{" +
                "year=" + year +
                ", blogNum=" + blogNum +
                '}';
    }
}
