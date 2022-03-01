package com.dabin.vo;

import com.dabin.entity.Tag;
import lombok.Data;

/**
 * @author: 程序员大彬
 * @time: 2021-12-26 21:46
 */
@Data
public class BlogTagVo extends Tag {
    private int blogId;
}
