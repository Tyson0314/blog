package com.dabin.dao;

import com.dabin.entity.Tag;
import com.dabin.entity.TagExample;
import com.dabin.vo.BlogTagVo;
import com.dabin.vo.TagWithCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {
    long countByExample(TagExample example);

    int deleteByExample(TagExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Tag record);

    int insertSelective(Tag record);

    List<Tag> selectByExample(TagExample example);

    Tag selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Tag record, @Param("example") TagExample example);

    int updateByExample(@Param("record") Tag record, @Param("example") TagExample example);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);


    /**
     * 以下是自定义的sql
     */
    Tag queryByName(Tag Tag);

    Integer updateTag(Tag Tag);

    Tag queryById(Tag Tag);

    int deleteById(Tag Tag);

    List<TagWithCountVO> selectTagList();

    List<TagWithCountVO> selectCategoryList();

    List<Tag> selectTagsOfBlog(Integer blogId);

    List<BlogTagVo> selectTagsByBlogIds(@Param("blogIdList") List<Integer> blogIdList);
}
