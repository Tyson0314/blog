package com.dabin.controller.manage;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.dto.BlogAddRequest;
import com.dabin.dto.BlogListRequest;
import com.dabin.service.impl.BlogServiceImpl;
import com.dabin.vo.BlogDetail4AdminVO;
import com.dabin.vo.BlogDetail4UserVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 博客管理端Controller
 *
 * @author 大彬
 * @date 2021-07-02 22:18
 */
@RequestMapping("manage/blogs")
@Api(description = "博客管理模块")
@RestController
public class ManageBlogController extends BaseController {

    @Autowired
    private BlogServiceImpl blogService;

    @ApiOperation(value = "新增博客")
    @PostMapping
    public Result<Boolean> addBlog(HttpServletRequest request,
                                   @RequestBody BlogAddRequest blogAddRequest) {
//        validate(blogAddRequest);

        return blogService.createBlogInfo(request, blogAddRequest);
    }

    @GetMapping
    @ApiOperation(value = "分页查询列表博客", response = BlogDetail4UserVO.class, responseContainer = "List")
    public Result<PageInfo<BlogDetail4UserVO>> getBlogList(@ModelAttribute BlogListRequest blogListRequest) {

        return blogService.getBlogListInfos(blogListRequest);
    }

    @PutMapping("/{id:\\d+}")
    @ApiOperation(value = "修改博客")
    public Result<Boolean> updateBlog(@ApiParam("博客id") @PathVariable Integer id,
                                      @RequestBody BlogAddRequest blogAddRequest) {
        validate(blogAddRequest);

        return blogService.updateBlog(id, blogAddRequest);
    }

    @GetMapping("/{id:\\d+}")
    @ApiOperation(value = "查找博客", response = BlogDetail4UserVO.class)
    public Result<BlogDetail4AdminVO> getBlog(@ApiParam("博客id") @PathVariable Integer id) {
        return blogService.getBlogDetailInfo(id);
    }

    @DeleteMapping("/{id:\\d+}")
    @ApiOperation(value = "删除博客")
    public Result<Boolean> deleteBlog(@ApiParam("博客id") @PathVariable Integer id) {

        return blogService.deleteBlogInfo(id);
    }

}
