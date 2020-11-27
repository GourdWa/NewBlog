package com.hzx.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.Tag;
import com.hzx.blog.bean.Type;

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
     * 保存标签，这个方法是用于从新增博客或者编辑博客中新增的标签
     * @param tagName
     * @return
     */
    Tag save(String tagName);

    /**
     * 保存标签，这个方法是单独从tag_input的保存，加了重名校验
     * @param tag
     * @return
     */
    boolean save(Tag tag);
    /**
     * 根据标签名称获取实例
     * @param tagName
     * @return
     */
    Tag getByName(String tagName);
    /**
     * 根据标签id集合获取标签集合
     * @param idList
     * @return
     */
    List<Tag> getTagsByIds(List<Long> idList);

    /**
     * 分页展示标签
     * @param currentNo
     * @param pageSize
     * @return
     */
    Page<Tag> list(Integer currentNo, Integer pageSize);

    /**
     * 根据标签id删除标签
     * @param tagId
     * @return
     */
    boolean delete(Long tagId);

    /**
     * 根据标签id获取标签实例
     * @param id
     * @return
     */
    Tag getById(Long id);

    /**
     * 更新标签名称
     * @param newTag
     * @return
     */
    boolean update(Tag newTag);
}
