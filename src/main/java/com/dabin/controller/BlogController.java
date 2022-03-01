package com.dabin.controller;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.dto.BlogListRequest;
import com.dabin.service.BlogService;
import com.dabin.vo.BlogDetail4UserVO;
import com.dabin.vo.IndexVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.dabin.common.constants.SystemConstant.USER_ID;

/**
 * 博客相关接口
 *
 * @author 大彬
 * @date 2021-09-27 0:22
 */
@Api("博客相关接口")
@RequestMapping("/blogs")
@CrossOrigin
@RestController
public class BlogController extends BaseController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    @ApiOperation(value = "分页查询列表博客", response = BlogDetail4UserVO.class, responseContainer = "List")
    public Result<PageInfo<BlogDetail4UserVO>> getBlogListInfos(@ModelAttribute BlogListRequest queryRequest) {

        return blogService.getBlogListInfos(queryRequest);
    }

    @GetMapping("/{id:\\d+}")
    @ApiOperation(value = "获取博客详情", response = BlogDetail4UserVO.class)
    public Result<BlogDetail4UserVO> getBlog(HttpServletRequest request, @ApiParam("博客id") @PathVariable Integer id) {
        String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID).toString();
        return blogService.getBlogDetailsInfo(id, userId);
    }

    @GetMapping("/index")
    @ApiOperation(value = "首页 内容初始化", response = Boolean.class)
    public Result<IndexVO> index() {

        return blogService.getIndexVO(true);
    }

    @GetMapping("/left")
    @ApiOperation(value = "侧边栏内容初始化", response = Boolean.class)
    public Result<IndexVO> left() {

        return blogService.getIndexVO(false);
    }

}
