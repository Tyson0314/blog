package com.dabin.service;

import com.dabin.common.base.Result;
import com.dabin.common.constants.TagType;
import com.dabin.dto.TagAddRequest;
import com.dabin.dto.TagListRequest;
import com.dabin.dto.TagUpdateRequest;
import com.dabin.vo.BlogTagVo;
import com.dabin.vo.KeyValueVO;
import com.dabin.vo.TagVO;
import com.dabin.vo.TagWithCountVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface TagService {

    /**
     * 标签列表-管理台
     */
    Result<PageInfo<TagVO>> getTagListInfos(TagListRequest tagListRequest);

    /**
     * 标签列表
     */
    Result<List<TagVO>> getAllTagListInfsByType(Integer tagType);


    /**
     * 获取标签列表
     */
    List<TagVO> getTagListByBlogId(Integer blogId);

    /**
     * 获取k-v结构标签信息
     */
    Result<List<KeyValueVO<Integer, String>>> getAllTagList(Integer tagType);

    /**
     * 创建标签
     */
    Result<Boolean> createTagInfo(TagAddRequest tagAddRequest);

    /**
     * 更新标签
     */
    Result<Boolean> updateTagInfo(Integer id, TagUpdateRequest updateRequest);

    /**
     * 获取标签详情
     */
    Result<TagVO> getTagDetailInfoById(Integer id);

    /**
     * 删除标签
     */
    Result<TagVO> deleteTagInfoById(Integer id);

    /**
     * 博客绑定标签
     */
    boolean bindTags2Blog(Integer blogId, List<Integer> tagIds);

    /**
     * 删除博客关联的标签
     */
    boolean deleteTagsByBlogId(Integer blogId);

    /**
     * 根据类型获取标签
     */
    List<TagWithCountVO> getTagListByTagType(TagType tagType);

    /**
     * 根据博客Id列表查询标签
     */
    List<BlogTagVo> selectTagsByBlogIds(List<Integer> blogIds);

}
