package com.dabin.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 字段修改与添加
 *
 * @author 大彬
 * @date 2021-06-30 22:20
 */
@ApiModel("添加博客实体类")
@Data
public class BlogAddRequest extends BlogUpdateRequest{

}
