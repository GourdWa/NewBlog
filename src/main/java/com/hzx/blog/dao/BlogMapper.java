package com.hzx.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzx.blog.bean.Blog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-12-20:43
 */
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 无重复的返回已发布博客的类型id列表
     * @return
     */
    List<Integer> getPublishedTypeIds(Integer size);

    /**
     * 返回一个类型中发布的博客的数量
     * 比如获得发布的博客中，类型为2的博客的数量
     * @param typeId
     * @return
     */
    Integer getBlogNumByPublishedAndTypeId(Integer typeId);

    /**
     * 返回已经发布的博客的id集合
     * @return
     */
    List<Integer> getPublishedIdList();

    /**
     * 更新一个博客的浏览次数
     * @param id
     */
    void updateViews(Long id);

    /**
     * 获得最新发表的推荐博客，size指定获取的博客数量
     * @param size
     * @return
     */
    List<Blog> getRecommendBlog(int size);

    /**
     * 更新博客的点赞数
     * @param id
     * @param goodJobCnt
     * @return
     */
    boolean updateGoodJob(@Param("id") Long id,@Param("cnt") int goodJobCnt);
}
