package com.dabin.common.constants;

/**
 * 通用状态枚举
 *
 * @author 大彬
 * @date 2021-07-02 23:35
 **/
public enum StatusEnum {

    /**
     * 用来表示数据的有效性
     */
    INVALID("0", "无效/已删除"),
    VALID("1", "有效/可用");

    private String code;
    private String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }
}

