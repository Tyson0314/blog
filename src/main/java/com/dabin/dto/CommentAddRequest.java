package com.dabin.dto;

import com.dabin.common.constants.CommentType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 创建评论/回复 请求信息
 *
 * @author 大彬
 * @date 2021-07-04 22:00
 */
 @Data
@NoArgsConstructor
@ApiModel("评论")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommentAddRequest {

    @ApiModelProperty("被回复的评论id")
    private Integer pid;

    @ApiModelProperty("被评论主体id")
    @NotNull
    private Integer targetId;

    @ApiModelProperty("被评论主体类型，ARTICLE，COMMENT_REPLY，MESSAGE_BOARD")
    @NotNull
    private CommentType targetType;

    @ApiModelProperty("用户邮箱")
    @NotEmpty
    @Email(message = "邮箱格式不合法")
    private String userEmail;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("评论内容")
    @NotEmpty
    private String content;

}
