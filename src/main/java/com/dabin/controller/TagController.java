package com.dabin.controller;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.vo.TagVO;
import com.dabin.service.impl.TagServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签相关接口
 *
 * @author 大彬
 * @date 2021-11-21 0:03
 **/
@Api("标签/分类相关接口")
@RequestMapping("tags")
@RestController
public class TagController extends BaseController {

    @Autowired
    private TagServiceImpl tagService;

    @GetMapping("/{type:\\d+}")
    @ApiOperation(value = "获取标签列表", response = TagVO.class, responseContainer = "List")
    public Result<List<TagVO>> getTagList(@PathVariable Integer type) {

        return tagService.getAllTagListInfsByType(type);
    }
}
