package com.dabin.common.constants;

/**
 * 评论的类型，评论可以在博客，回复，留言版中
 *
 * @author 大彬
 * @date 2021-07-04 23:18
 */
public enum CommentType {

    /**
     * 评论的类型
     */
    ARTICLE(1, "博客"),
    COMMENT_REPLY(2, "评论回复"),
    MESSAGE_BOARD(3, "留言板");

    private int code;
    private String desc;

    CommentType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
