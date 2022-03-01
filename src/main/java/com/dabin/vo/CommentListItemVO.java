package com.dabin.vo;

import com.dabin.common.constants.CommentType;
import com.dabin.common.utils.DateTimeUtil;
import com.dabin.entity.Comment;
import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 评论列表vo
 *
 * @author 大彬
 * @date 2021-09-27 0:16
 */
@Data
@NoArgsConstructor
@ApiModel("评论类")
public class CommentListItemVO {

    private Integer id;

    /**
     * 被回复的id
     */
    private Integer pid;
    /**
     * 被评论目标的id
     */
    private Integer targetId;
    /**
     * 被评论目标的类型、评论/博客/留言版
     */
    private CommentType targetType;
    /**
     * 用户邮箱
     */
    private String userEmail;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userIcon;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer voteCount;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 评论下面的回复、回复之间的对话
     */
    private List<CommentListItemVO> childrenComments;

    /**
     * 当前用户是否点赞
     */
    private boolean voteStatus;

    /**
     * 构建vo根据do
     *
     * @param comment
     * @return
     */
    public static CommentListItemVO createFrom(Comment comment) {
        Preconditions.checkNotNull(comment, "构建vo的参数不能为null");
        CommentListItemVO targetVO = new CommentListItemVO();
        targetVO.setCreateTime(DateTimeUtil.dateToStr(comment.getCreateTime()));
        BeanUtils.copyProperties(comment, targetVO);
        return targetVO;
    }
}
