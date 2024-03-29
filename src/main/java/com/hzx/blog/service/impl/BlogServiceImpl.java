package com.hzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzx.blog.bean.*;
import com.hzx.blog.dao.BlogMapper;
import com.hzx.blog.exception.CommonException;
import com.hzx.blog.service.*;
import com.hzx.blog.utils.CheckUtil;
import com.hzx.blog.utils.MarkDownUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-11-13-20:03
 */
@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogTagService blogTagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    RedisTemplate typeRedisTemplate;
    @Autowired
    RedisTemplate tagRedisTemplate;
    @Autowired
    RedisTemplate blogRedisTemplate;

    /**
     * 删除Redis中所有的键值数据
     */
    public void flushRedis() {
        Set<String> keys = blogRedisTemplate.keys("*");
        Long deleteNum = blogRedisTemplate.delete(keys);
        return;
    }

    /**
     * 首先根据条件查询出分页数据，并配合Redis为博客的类型赋值
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    public Page<Blog> setTypeByRedis(Page<Blog> page, QueryWrapper<Blog> queryWrapper) {
        Page<Blog> blogPage = blogMapper.selectPage(page, queryWrapper);
        List<Blog> records = blogPage.getRecords();
        //依次为查询到的Blog的type赋值
        for (Blog record : records) {
            Type t = typeService.getById(record.getTypeId());
            record.setType(t);
        }
        return blogPage;
    }


    @Override
    public Page<Blog> list(int pageNo, int size) {
        Page<Blog> page = new Page<>(pageNo, size);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //根据更新时间排序
        queryWrapper.orderByDesc("update_time");
        return setTypeByRedis(page, queryWrapper);
    }


    @Override
    public Page<Blog> list(int pageNo, int size, String title, Integer typeId, boolean recommend) {
        Page<Blog> page = new Page<>(pageNo, size);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //查询前先去除名称两边的空格
        if (title != null && title.trim().length() > 0)
            queryWrapper.like("title", title.trim());
        if (typeId != null)
            queryWrapper.eq("type_id", typeId);
        if (recommend)
            queryWrapper.eq("recommend", true);
        //根据更新时间排序
        queryWrapper.orderByDesc("update_time");
        return setTypeByRedis(page, queryWrapper);
    }

    @Transactional
    @Override
    public Blog save(Blog blog) {
        //设置创建时间和更新时间
        Date date = new Date();
        blog.setCreateTime(date);
        blog.setUpdateTime(date);
        //设置点赞数和浏览数
        blog.setGoodJob(0);
        blog.setViews(0);
        //将博客插入
        blogMapper.insert(blog);
        //建立博客和标签的映射
        String tagIds = blog.getTagIds();
        createBlogTagMap(tagIds, blog.getId());
        // 2020.12.24+ 清除缓存
        flushRedis();
        return blog;
    }

    @Transactional
    @Override
    public boolean delete(Long blogId) {
        int deleteA = blogMapper.deleteById(blogId);
        if (deleteA > 0) {
            // 2020.12.24+ 清除缓存
            flushRedis();
            // 2020.12.28+ 删除该博客时需要将该博客下的所有评论都要删除
            commentService.deleteByBlogId(blogId);
            return blogTagService.delete(blogId);
        }
        return false;
    }

    @Override
    public Blog getBlogById(Long blogId) {
        Blog blog = blogMapper.selectById(blogId);
        //对查询出来的博客tags和type进行赋值
        Type type = typeService.getById(blog.getTypeId());
        blog.setType(type);
        //封装tags
        List<BlogTag> blogTags = blogTagService.getTagIdsByBlogId(blogId);
        List<Long> idList = new ArrayList<>();
        StringBuilder tagIds = new StringBuilder();
        for (BlogTag blogTag : blogTags) {
            idList.add(blogTag.getTagId());
            tagIds.append(blogTag.getTagId()).append(",");
        }
        List<Tag> tags = tagService.getTagsByIds(idList);
        blog.setTags(tags);
        blog.setTagIds(tagIds.substring(0, tagIds.length() - 1));
        return blog;
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        //先获取出数据库中的博客
        Blog blog1 = blogMapper.selectById(id);
        blog.setCreateTime(blog1.getCreateTime());
        blog.setUpdateTime(new Date());
        blog.setGoodJob(blog1.getGoodJob());
        blog.setViews(blog1.getViews());
        //因为可能更改了标签，因此将以前的标签关联全部删除，并重新插入,,,
        blogTagService.delete(blog1.getId());
        //建立博客和标签的映射
        String tagIds = blog.getTagIds();
        createBlogTagMap(tagIds, blog.getId());
//        BeanUtils.copyProperties(blog, blog1);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", blog1.getId());
        blogMapper.update(blog, queryWrapper);
        // 2020.12.24+ 清除缓存
        flushRedis();
        return blog1;
    }

    @Override
    public List<Blog> getByTypeId(Long typeId) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id", typeId);
        List<Blog> blogs = blogMapper.selectList(queryWrapper);
        Type type = typeService.getById(typeId);
        blogs.forEach(blog -> blog.setType(type));
        return blogs;
    }


    public void createBlogTagMap(String tagIds, long blogId) {
        String[] ids = tagIds.split(",");
        for (String tagId : ids) {
            //如果不是数字，说明这个标签是新插入的，需要将这个标签保存进数据库并更新Redis
            if (!CheckUtil.checkNum(tagId.trim())) {
                Tag saveTag = tagService.save(tagId);
                blogTagService.save(blogId, saveTag.getId());
            } else {
                //如果是数字，说明这个标签之前存在，直接查询即可
                long newId = Long.parseLong(tagId);
                blogTagService.save(blogId, newId);
            }
        }
    }

    /***************************************************前端展示实现***************************************************/
    /**
     * 为博客设置类型和标签
     *
     * @param page
     * @param queryWrapper
     * @return
     */
    private Page<Blog> setTypeAndTags(Page<Blog> page, QueryWrapper<Blog> queryWrapper) {
        Page<Blog> blogPage = blogMapper.selectPage(page, queryWrapper);
        List<Blog> records = blogPage.getRecords();
        for (Blog record : records) {
            //设置作者
            User user = userService.getUserById(record.getUserId());
            record.setUser(user);
            //先为类型和标签赋值
            setTypeAndTags(record);
        }
        return blogPage;
    }

    /**
     * 传入一个博客，为该博客设置类型和标签
     *
     * @param blog
     * @return
     */
    private void setTypeAndTags(Blog blog) {
        Type type = typeService.getById(blog.getTypeId());
        blog.setType(type);
        List<BlogTag> blogTags = blogTagService.getTagIdsByBlogId(blog.getId());
        List<Tag> tags = new ArrayList<>();
        for (BlogTag blogTag : blogTags) {
            Tag tag = tagService.getById(blogTag.getTagId());
            tags.add(tag);
        }
        blog.setTags(tags);
    }

    @Override
    public List<Integer> getPublishedTypeIds(int size) {
        return blogMapper.getPublishedTypeIds(size);
    }

    @Override
    public Integer getBlogNumByPublishedAndTypeId(int typeId) {
        return blogMapper.getBlogNumByPublishedAndTypeId(typeId);
    }

    @Override
    public Page<Blog> getBlogsByTypeIdTop(Long typeId, Page<Blog> page) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        // 2021.05.24修改为按创建时间排序
        queryWrapper.eq("type_id", typeId).eq("published", true).orderByDesc("create_time");
        //为博客设置类型和标签
        Page<Blog> blogPage = setTypeAndTags(page, queryWrapper);
        //防止人为输入的页面超过了总页面的数量
        if (blogPage.getCurrent() > blogPage.getPages() && blogPage.getPages() != 0)
            blogPage = getBlogsByTypeIdTop(typeId, new Page<>(blogPage.getPages(), blogPage.getSize()));
        return blogPage;
    }

    @Override
    public List<Integer> getPublishedIdList() {
        return blogMapper.getPublishedIdList();
    }

    @Override
    public Page<Blog> getBlogsByIdsTop(List<Long> blogIds, Page<Blog> page) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        // 2021.05.24修改为按创建时间排序
        queryWrapper.eq("published", true).in("id", blogIds).orderByDesc("create_time");
        Page<Blog> blogPage = setTypeAndTags(page, queryWrapper);
        // 2020.12.11添加
        //防止人为输入的页面超过了总页面的数量
        if (blogPage.getCurrent() > blogPage.getPages() && blogPage.getPages() != 0)
            blogPage = getBlogsByIdsTop(blogIds, new Page<>(blogPage.getPages(), blogPage.getSize()));
        return blogPage;
    }

    @Override
    public boolean updateGoodJob(Blog blog) {
        boolean success = blogMapper.updateGoodJob(blog.getId(), blog.getGoodJob() + 1);
        return success;
    }


    @Override
    public Page<Blog> listTop(Integer currentNo, Integer pageSize) {
        Page<Blog> page = new Page<>(currentNo, pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        //根据更新时间排序
        // 2021.05.24修改为按创建时间排序
        queryWrapper.orderByDesc("create_time");
        //只返回已经发布的博客，保存的博客不要展示出来
        queryWrapper.eq("published", true);
        //查询出来之后还要为这些博客的标签和类型赋值+作者
        Page<Blog> blogPage = setTypeAndTags(page, queryWrapper);
        //2020.12.11+
        //防止人为输入的页面超过了总页面的数量
        if (blogPage.getCurrent() > blogPage.getPages() && blogPage.getPages() != 0)
            blogPage = listTop((int) blogPage.getPages(), pageSize);
        return blogPage;
    }

    /**
     * TODO
     * 加缓存
     *
     * @param size
     * @return
     */
    @Override
    public List<Blog> listRecommendBlogTop(int size) {
        List<Blog> blogs = (List<Blog>) blogRedisTemplate.opsForValue().get("recommendBlog");
        if (blogs == null) {
            blogs = blogMapper.getRecommendBlog(size);
            blogRedisTemplate.opsForValue().set("recommendBlog", blogs, 1, TimeUnit.HOURS);
        }
        return blogs;
    }

    /**
     * TODO
     * 加缓存
     *
     * @param id
     * @return
     */
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = (Blog) blogRedisTemplate.opsForValue().get("blog_" + id);
        if (blog != null) {
            /*
            * 2020.12.29+ 更新浏览次数
            * */
            blogMapper.updateViews(id);
            /*
            * 2020.12.28+ 从Redis中读出数据后还要更新浏览次数和点赞数
            * */
            Blog blogFromDB = blogMapper.selectById(id);
            blog.setViews(blogFromDB.getViews());
            blog.setGoodJob(blogFromDB.getGoodJob());
            return blog;
        } else {
            blog = blogMapper.selectById(id);
            if (blog == null)
                throw new CommonException("该博客不存在");
            Blog newBlog = new Blog();
            BeanUtils.copyProperties(blog, newBlog);
            String content = newBlog.getContent();
            String htmlContent = MarkDownUtil.markdownToHtmlExtensions(content);
            newBlog.setContent(htmlContent);
            //更新浏览次数
            blogMapper.updateViews(id);
            //设置类型和标签
            setTypeAndTags(newBlog);
            User user = userService.getUserById(newBlog.getUserId());
            newBlog.setUser(user);
            blogRedisTemplate.opsForValue().set("blog_" + id, newBlog, 1, TimeUnit.HOURS);
            return newBlog;
        }
    }

    @Override
    public Page<Blog> listBlogTop(Integer currentNo, Integer pageSize, String query) {
        Page<Blog> page = new Page<>(currentNo, pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("published", true).
                and(w -> w.like("title", query).or().like("content", query)).
                orderByDesc("create_time");
        Page<Blog> blogPage = setTypeAndTags(page, queryWrapper);
        //2020.12.11+
        //防止人为输入的页面超过了总页面的数量
        if (blogPage.getCurrent() > blogPage.getPages() && blogPage.getPages() != 0)
            blogPage = listBlogTop((int) blogPage.getPages(), pageSize, query);
        return blogPage;
    }

    /***************************************************归档页面用***************************************************/
    @Override
    public List<Archive> getYearList() {
        List<Archive> archives = blogMapper.getArchive();
        return archives;
    }

    @Override
    public Page<Blog> getBlogByYear(Integer currentNo, Integer pageSize, Integer year) {
        Page<Blog> page = new Page<>(currentNo, pageSize);
        Page<Blog> blogPage = blogMapper.getBlogByYear(page, year);
        if (blogPage.getRecords().size() == 0)
            throw new CommonException("搞事情？");
        return blogPage;
    }
}
