package com.dabin.entity;

import lombok.Data;

@Data
public class Blog extends BaseEntity {

    private Integer categoryId;

    private String title;

    private String summary;

    private String imgUrl;

    private String blogCatalog;

    private String author;

    private Integer code;

    private Integer viewCount;

    private Integer commentCount;

    private Byte blogStatus;

    private String htmlContent;

    private String content;

    private Integer voteCount;

    /**
     * 当前用户是否点赞
     */
    private boolean voteStatus;

    /**
     * 作者姓名
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String avatarUrl;
}
