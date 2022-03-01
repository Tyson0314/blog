package com.dabin.dto;

 import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 标签添加 请求
 *
 * @author 大彬
 * @date 2021-11-18 20:17
 **/
@ApiModel(value = "标签对象")
@Data
public class TagAddRequest extends TagUpdateRequest{

}
