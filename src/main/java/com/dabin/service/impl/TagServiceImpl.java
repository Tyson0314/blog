package com.dabin.service.impl;

import com.dabin.common.base.BaseService;
import com.dabin.common.base.Result;
import com.dabin.common.constants.TagType;
import com.dabin.dao.BlogMapper;
import com.dabin.dao.BlogTagMapper;
import com.dabin.dao.TagMapper;
import com.dabin.dto.TagAddRequest;
import com.dabin.dto.TagListRequest;
import com.dabin.dto.TagUpdateRequest;
import com.dabin.entity.*;
import com.dabin.service.TagService;
import com.dabin.vo.BlogTagVo;
import com.dabin.vo.KeyValueVO;
import com.dabin.vo.TagVO;
import com.dabin.vo.TagWithCountVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 标签service
 *
 * @author 大彬
 * @date 2021-07-03 23:42
 */
@Service
public class TagServiceImpl extends BaseService implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private BlogTagMapper blogTagMapper;

    @Resource
    private BlogMapper blogMapper;

    /**
     * 获取标签列表信息
     *
     * @param tagListRequest 标签查询请求
     * @return
     */
    @Override
    public Result<PageInfo<TagVO>> getTagListInfos(TagListRequest tagListRequest) {
        Preconditions.checkNotNull(tagListRequest, "请求参数不能为null");

        Page page = getPage(tagListRequest);
        TagExample example = new TagExample();
        if (Objects.nonNull(tagListRequest.getTagType())) {
            example.createCriteria().andTagTypeEqualTo(tagListRequest.getTagType());
        }
        List<Tag> tagList = tagMapper.selectByExample(example);
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(tagList.stream().map(TagVO::createFrom).collect(Collectors.toList()));

        return resultOk(pageInfo);
    }

    /**
     * 获取tagVo列表
     *
     * @param tagType 类型
     * @return
     */
    @Override
    public Result<List<TagVO>> getAllTagListInfsByType(Integer tagType) {

        return resultOk(getAllTagsByType(tagType).stream().map(TagVO::createFrom).collect(Collectors.toList()));
    }

    /**
     * 获取所有的k-v结构标签信息
     *
     * @param tagType
     * @return
     */
    @Override
    public Result<List<KeyValueVO<Integer, String>>> getAllTagList(Integer tagType) {

        return resultOk(getAllTagsByType(tagType).stream().map(tagDO -> new KeyValueVO<Integer, String>(tagDO.getId(), tagDO.getTagName())).collect(Collectors.toList()));
    }

    /**
     * 获取所有的标签，根据id
     *
     * @param tagType
     * @return
     */
    private List<Tag> getAllTagsByType(Integer tagType) {
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andTagTypeEqualTo(tagType);

        return tagMapper.selectByExample(tagExample);
    }

    /**
     * 添加标签
     *
     * @param tagAddRequest 请求参数
     * @return
     */
    @Override
    public Result<Boolean> createTagInfo(TagAddRequest tagAddRequest) {
        Tag tag = new Tag();
        Result<Boolean> validateResult = validateAndInitCreateRequest(tagAddRequest, tag);
        if (!validateResult.isOk()) {
            return validateResult;
        }

        if (tagMapper.insert(tag) == 0) {
            return resultError4DB("添加标签失败");
        }
        return resultOk();
    }


    /**
     * todo 验证添加参数
     *
     * @param tagAddRequest 请求参数
     * @param tag
     * @return
     */
    private Result<Boolean> validateAndInitCreateRequest(TagAddRequest tagAddRequest, Tag tag) {
        Preconditions.checkNotNull(tag, "要添加的参数不能为null");
        tag.setCreateTime(new Date());
        return validateAndInitUpdateRequest(tagAddRequest, tag);
    }

    /**
     * 更新标签
     *
     * @param id            主键id
     * @param updateRequest 请求参数
     * @return
     */
    @Override
    public Result<Boolean> updateTagInfo(Integer id, TagUpdateRequest updateRequest) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        Tag tag = tagMapper.selectByPrimaryKey(id);
        Result<Boolean> validateResult = validateAndInitUpdateRequest(updateRequest, tag);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        if (tagMapper.updateByPrimaryKey(tag) == 0) {
            return resultError4DB("更新失败");
        }
        return resultOk();
    }

    /**
     * 逻辑校验
     *
     * @param updateRequest
     * @param tag
     * @return
     */
    public Result<Boolean> validateAndInitUpdateRequest(TagUpdateRequest updateRequest, Tag tag) {
        Preconditions.checkNotNull(updateRequest, "更新参数不能为null");
        if (Objects.isNull(tag)) {
            return resultError4Param("要修改的数据不存在");
        }
        // 是否重名
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andTagNameEqualTo(updateRequest.getTagName());
        List<Tag> tagList = tagMapper.selectByExample(tagExample);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (Tag tempDO : tagList) {
                if (Objects.equals(tempDO.getTagType(), tag.getTagType())) {
                    return resultError4Param("名称为[" + tag.getTagName() + "]的标签已存在");
                }
            }
        }
        tag.setTagName(updateRequest.getTagName());
        tag.setTagDesc(updateRequest.getTagDesc());
        tag.setTagType(updateRequest.getTagType());
        tag.setUpdateTime(new Date());

        return resultOk();
    }

    /**
     * 根据id获取详情
     *
     * @param id 主键id
     * @return
     */
    @Override
    public Result<TagVO> getTagDetailInfoById(Integer id) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        Tag tag = tagMapper.selectByPrimaryKey(id);
        if (Objects.isNull(tag)) {
            return resultError4Param("该标签不存在");
        }

        return resultOk(TagVO.createFrom(tagMapper.selectByPrimaryKey(id)));
    }

    public List<Tag> getTagListByTypeAndIds(TagType tagType, List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andTagTypeEqualTo(tagType.getCode()).andIdIn(ids);
        if (Objects.nonNull(tagType)) {
            tagExample.createCriteria().andTagTypeEqualTo(tagType.getCode());
        }

        return tagMapper.selectByExample(tagExample);
    }

    /**
     * 根据主键id删除标签
     *
     * @param id 主键id
     * @return
     */
    @Override
    public Result<TagVO> deleteTagInfoById(Integer id) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        BlogTagExample example = new BlogTagExample();
        example.createCriteria().andTagIdEqualTo(id);
        BlogExample blogExample = new BlogExample();
        blogExample.createCriteria().andCategoryIdEqualTo(id);

        if (CollectionUtils.isNotEmpty(blogTagMapper.selectByExample(example))
                || CollectionUtils.isNotEmpty((blogTagMapper.selectByExample(example)))) {
            return resultError4Param("改标签正在被使用");
        }
        Tag targetDO = tagMapper.selectByPrimaryKey(id);
        if (Objects.isNull(targetDO)) {
            return resultOk();
        }
        if (tagMapper.deleteByPrimaryKey(id) == 0) {
            return resultError4DB("删除失败");
        }
        return resultOk();
    }

    @Override
    public List<TagVO> getTagListByBlogId(Integer blogId) {
        if (Objects.isNull(blogId)) {
            return Lists.newArrayList();
        }

        return tagMapper.selectTagsOfBlog(blogId)
                .stream()
                .filter(Objects::nonNull)
                .map(TagVO::createFrom)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取标签
     *
     * @param tagType   标签类型
     * @return
     */
    @Override
    public List<TagWithCountVO> getTagListByTagType(TagType tagType) {
        Preconditions.checkNotNull(tagType, "标签类型不能为null");
        if (Objects.equals(tagType, TagType.CATEGORY)) {
            return tagMapper.selectCategoryList();
        } else {
            return tagMapper.selectTagList();
        }
    }


    /**
     * 删除博客关联的标签
     *
     * @param blogId
     */
    @Override
    public boolean deleteTagsByBlogId(Integer blogId) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");

        BlogTagExample example = new BlogTagExample();
        example.createCriteria().andBlogIdEqualTo(blogId);
        return blogTagMapper.deleteByExample(example) > 0;
    }

    /**
     * 给博客绑定标签
     *
     * @param blogId    博客id
     * @param tagIds    标签集合
     * @return
     */
    @Override
    public boolean bindTags2Blog(Integer blogId, List<Integer> tagIds) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(tagIds), "标签id列表不能为空");

        AtomicInteger count = new AtomicInteger();
        tagIds.stream().forEach(tagId -> {
            count.addAndGet(blogTagMapper.insert(new BlogTag(null, tagId, blogId)));
        });

        return count.intValue() == tagIds.size();
    }

    @Override
    public List<BlogTagVo> selectTagsByBlogIds( List<Integer> blogIds) {
        return tagMapper.selectTagsByBlogIds(blogIds);
    }

}
