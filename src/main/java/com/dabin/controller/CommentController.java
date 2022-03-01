package com.dabin.controller;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.dto.CommentAddRequest;
import com.dabin.dto.CommentListRequest;
import com.dabin.service.CommentService;
import com.dabin.vo.CommentListItemVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.dabin.common.constants.SystemConstant.USER_ID;


/**
 * 评论相关接口
 *
 * @author 大彬
 * @date 2021-11-20 12:20
 **/
@Slf4j
@Api(description = "用户评论模块")
@RequestMapping("/comments")
@CrossOrigin
@RestController
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ApiOperation(value = "添加评论")
    public Result addComment(HttpServletRequest request, @RequestBody CommentAddRequest commentAddRequest) {
        log.info("addComment:{}", commentAddRequest);
        return commentService.addCommentInfo(request, commentAddRequest);
    }

    @GetMapping("/{targetId:\\d+}")
    @ApiOperation(value = "获取评论列表", responseContainer = "PageInfo", response = CommentListItemVO.class)
    public Result<PageInfo<CommentListItemVO>> getCommentListInfos(HttpServletRequest request,
                                                                   @PathVariable Integer targetId,
                                                                   CommentListRequest commentListRequest) {
        String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID).toString();
        return commentService.getCommentInfos(targetId, userId, commentListRequest);
    }

}
