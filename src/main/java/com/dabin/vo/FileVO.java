package com.dabin.vo;

import lombok.Data;

/**
 * @author: 程序员大彬
 * @time: 2022-01-11 22:34
 */
@Data
public class FileVO {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 图片Url集合
     */
    private String url;

    /**
     * 上传图片时携带的token令牌
     */
    private String token;
}
