package com.dabin.dto;

import com.dabin.common.constants.CommentType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 评论列表请求参数
 *
 * @author 大彬
 * @date 2021-07-04 21:50
 **/
@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommentListRequest extends PageInfo4Request {

    /**
     * 父级评论id
     */
    @ApiModelProperty("父级评论id，当查询一个留言下面的回复，对话时使用")
    private Integer pid;

    /**
     * 被评论主体类型
     */
    @ApiModelProperty("被评论主体类型")
    @NotNull
    private CommentType targetType;
}
