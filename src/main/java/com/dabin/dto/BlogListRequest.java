package com.dabin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 博客查询参数bean
 *
 * @author 大彬
 * @date 2021-07-02 2:26
 **/
@ApiModel("博客列表查询参数")
@Data
public class BlogListRequest extends PageInfo4Request {

    /**
     * 分类id
     */
    @ApiModelProperty(name = "分类id")
    private Integer categoryId;

    /**
     * 标签id
     */
    @ApiModelProperty(name = "标签id")
    private Integer tagId;

    /**
     * 状态吗
     */
    @ApiModelProperty(name = "状态")
    private Byte status;

}
