package com.dabin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 目录项， 文章详情页用
 *
 * @author 大彬
 * @date 2021-08-18 10:10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItem {
    /**
     * 主键id
     */
    private String id;

    /**
     * 内容
     */
    private String text;

    /**
     * 目录的层级
     */
    private Integer lev;

}
