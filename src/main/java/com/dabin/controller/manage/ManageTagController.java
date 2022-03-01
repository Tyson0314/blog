package com.dabin.controller.manage;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.dto.TagAddRequest;
import com.dabin.dto.TagListRequest;
import com.dabin.dto.TagUpdateRequest;
import com.dabin.vo.TagVO;
import com.dabin.service.impl.TagServiceImpl;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 标签管理controller
 *
 * @author 大彬
 * @date 2021-11-18 16:36
 **/

@Api(description = "标签管理")
@RequestMapping("manage/tags")
@RestController
public class ManageTagController extends BaseController {

    @Autowired
    private TagServiceImpl tagService;

    @GetMapping
    @ApiOperation(value = "获取标签列表", response = TagVO.class, responseContainer = "List")
    public Result<PageInfo<TagVO>> tagList(@ModelAttribute TagListRequest tagListRequest) {

        return tagService.getTagListInfos(tagListRequest);
    }

    @PostMapping
    @ApiOperation(value = "添加标签", response = Boolean.class)
    public Result<Boolean> addTag(@RequestBody TagAddRequest tagAddRequest) {
        validate(tagAddRequest);

        return tagService.createTagInfo(tagAddRequest);
    }

    @PutMapping("/{id:\\d+}")
    @ApiOperation(value = "修改标签", response = Boolean.class)
    public Result<Boolean> updateTag(@PathVariable(name = "id") Integer id, @RequestBody TagUpdateRequest tagUpdateRequest) {
        validate(tagUpdateRequest);

        return tagService.updateTagInfo(id, tagUpdateRequest);
    }

    @GetMapping("/{id:\\d+}")
    @ApiOperation(value = "查找标签", response = Boolean.class)
    public Result<TagVO> selectTag(@PathVariable(name = "id") Integer id) {

        return tagService.getTagDetailInfoById(id);
    }

    @DeleteMapping("/{id:\\d+}")
    @ApiOperation(value = "删除标签", response = Boolean.class)
    public Result<TagVO> deleteTag(@PathVariable(name = "id") Integer id) {

        return tagService.deleteTagInfoById(id);
    }

}
