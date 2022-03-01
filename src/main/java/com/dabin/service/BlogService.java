package com.dabin.service;

import com.dabin.common.base.Result;
import com.dabin.dto.BlogAddRequest;
import com.dabin.dto.BlogListRequest;
import com.dabin.dto.BlogUpdateRequest;
import com.dabin.entity.Blog;
import com.dabin.vo.BlogDetail4AdminVO;
import com.dabin.vo.BlogDetail4UserVO;
import com.dabin.vo.IndexVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;

public interface BlogService {
    /**
     * 新增博客
     */
    public Result<Boolean> createBlogInfo(HttpServletRequest request, BlogAddRequest addRequest);

    /**
     * 更新博客
     */
    public Result<Boolean> updateBlog(Integer id, BlogUpdateRequest updateRequest);

    /**
     * 分页查询博客
     */
    public Result<PageInfo<BlogDetail4UserVO>> getBlogListInfos(BlogListRequest queryRequest);

    /**
     * 博客详情
     */
    public Result<BlogDetail4UserVO> getBlogDetailsInfo(Integer id, String userId);

    /**
     * 更新统计数据
     */
    public Result updateBlogCount(Integer blogId, String userId, String type);

    /**
     * 获取首页数据
     */
    public Result<IndexVO> getIndexVO(boolean withBlogs);

    /**
     * 博客详情-管理台
     */
    public Result<BlogDetail4AdminVO> getBlogDetailInfo(Integer blogId);

    /**
     * 删除博客
     */
    public Result<Boolean> deleteBlogInfo(Integer id);

    /**
     * 根据Id查询
     */
    public Blog getBlogById(Integer id);

    /**
     * 同步点赞数据到db
     */
    void transVoteDataFromRedis2DB();
}
