package com.dabin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博客do
 *
 * @author 大彬
 * @date 2021-07-09 22:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogWithBLOBs extends Blog {

    private String content;

    private String htmlContent;
}
