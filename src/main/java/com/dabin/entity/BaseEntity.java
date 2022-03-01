package com.dabin.entity;

import lombok.Data;

import java.util.Date;

/**
 * do类的基类
 *
 * @author 大彬
 * @date 2021-07-03 23:34
 **/
@Data
public class BaseEntity {

    private Integer id;

    private Date createTime;

    private Date updateTime;
}
