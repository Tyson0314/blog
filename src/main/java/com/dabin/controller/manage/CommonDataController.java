package com.dabin.controller.manage;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.common.constants.TagType;
import com.dabin.service.impl.TagServiceImpl;
import com.dabin.vo.KeyValueVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共数据接口
 *
 * @author 大彬
 * @date 2021-04-19 22:54
 **/
@Api("公共数据接口")
@RestController
@RequestMapping("/common")
public class CommonDataController extends BaseController {

    @Autowired
    private TagServiceImpl tagService;

    /**
     * 获取所有博客分类
     *
     * @return
     */
    @GetMapping("/all/categories")
    @ApiOperation(value = "获取博客分类", response = KeyValueVO.class)
    public Result<List<KeyValueVO<Integer, String>>> getAllCategoryInfos() {

        return tagService.getAllTagList(TagType.CATEGORY.getCode());
    }

    /**
     * 获取所有博客标签
     */
    @GetMapping("/all/tags")
    public Result<List<KeyValueVO<Integer, String>>> getAllTagInfos() {

        return tagService.getAllTagList(TagType.TAG.getCode());
    }
}
