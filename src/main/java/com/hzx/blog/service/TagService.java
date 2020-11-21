package com.hzx.blog.service;

import com.hzx.blog.bean.Tag;

import java.util.List;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-18-10:20
 */
public interface TagService {
    /**
     * 无条件列出所有的标签
     *
     * @return
     */
    public List<Tag> list();

    /**
     * 保存标签，保存完之后更新Redis
     * @param tagName
     * @return
     */
    Tag save(String tagName);

    /**
     * 根据标签id集合获取标签集合
     * @param idList
     * @return
     */
    List<Tag> getTagsByIds(List<Long> idList);


}
