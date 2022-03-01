package com.dabin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 标签列表查询接口
 *
 * @author 大彬
 * @date 2021-07-03 22:15
 **/

@Data
public class TagListRequest extends PageInfo4Request {
    /**
     * 标签类型
     */
    @JsonProperty(value = "tag_type")
    private Integer tagType;
}
