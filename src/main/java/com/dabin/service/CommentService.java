package com.dabin.service;

import com.dabin.common.base.Result;
import com.dabin.dto.CommentAddRequest;
import com.dabin.dto.CommentListRequest;
import com.dabin.vo.CommentListItemVO;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: 程序员大彬
 * @time: 2021-12-11 20:31
 */
public interface CommentService {
    /**
     * 添加评论
     */
    Result<Boolean> addCommentInfo(HttpServletRequest request, CommentAddRequest commentAddRequest);

    /**
     * 获取评论列表
     */
    Result<PageInfo<CommentListItemVO>> getCommentInfos(Integer targetId, String userId, CommentListRequest commentListRequest);

    /**
     * 删除评论
     */
    Result<Boolean> deleteCommentInfo(Integer commentId);

    /**
     * 同步点赞数据到db
     */
    void transVoteDataFromRedis2DB();
}
