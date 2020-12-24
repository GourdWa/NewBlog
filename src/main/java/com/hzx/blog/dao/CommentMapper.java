package com.hzx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzx.blog.bean.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-12-20:45
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
