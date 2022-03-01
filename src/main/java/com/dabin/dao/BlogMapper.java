package com.dabin.dao;

import com.dabin.dto.BlogAddRequest;
import com.dabin.dto.BlogListRequest;
import com.dabin.entity.Blog;
import com.dabin.entity.BlogExample;
import com.dabin.entity.BlogWithBLOBs;
import com.dabin.vo.BlogDetail4UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlogMapper {
    long countByExample(BlogExample example);

    int deleteByExample(BlogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BlogWithBLOBs record);

    int insertSelective(BlogWithBLOBs record);

    List<BlogWithBLOBs> selectByExampleWithBLOBs(BlogExample example);

    List<Blog> selectByExample(BlogExample example);

    BlogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BlogWithBLOBs record, @Param("example") BlogExample example);

    int updateByExampleWithBLOBs(@Param("record") BlogWithBLOBs record, @Param("example") BlogExample example);

    int updateByExample(@Param("record") Blog record, @Param("example") BlogExample example);

    int updateByPrimaryKeySelective(BlogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(BlogWithBLOBs record);

    int updateByPrimaryKey(Blog record);

    BlogWithBLOBs selectByIdAndStatus(@Param("id") Integer id, @Param("blogStatus") Byte blogStatus);

    List<Blog> selectBlogs(BlogListRequest blogListRequest);

    List<BlogDetail4UserVO> selectHotOrRecommendBlogs(@Param("code") int code, @Param("limit") int limit);

    /**
     * 以下是自定义sql
     */

    int updateBlog(BlogAddRequest blogAddRequest);

    /**
     * id 上一条-1 或者 下一条1
     */
    Blog selectLastOrNext(@Param("id")Integer id, @Param("isLast")boolean isLast);


    int updateBlogCount(@Param("blogId") Integer id, @Param("type") String type);

    int batchInsert(@Param("blogList") List<Blog> blogList);
}
