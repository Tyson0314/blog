package com.dabin.controller;

import com.dabin.common.base.Result;
import com.dabin.entity.Vote;
import com.dabin.service.VoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.dabin.common.constants.SystemConstant.USER_ID;

/**
 * @author: 程序员大彬
 * @time: 2021-12-23 22:19
 */
@Api("点赞接口")
@RequestMapping("/vote")
@CrossOrigin
@RestController
public class VoteController {

    @Autowired
    VoteService voteService;

    @PutMapping("/{id:\\d+}")
    @ApiOperation(value = "修改博客统计，如点赞，浏览量，评论数", response = Boolean.class)
    public Result<Integer> vote(HttpServletRequest request,
                                @ApiParam("博客id") @PathVariable Integer id,
                                @RequestBody Vote vote) {
        if (vote == null) {
            return Result.createByErrorMessage("参数为空");
        }
        String userId = request.getAttribute(USER_ID) == null ? "" : request.getAttribute(USER_ID).toString();
        return voteService.vote(id, userId, vote.getType());
    }

}
