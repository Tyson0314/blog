package com.dabin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * k-v结果封装
 *
 * @author 大彬
 * @date 2021-04-19 22:57
 **/
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyValueVO<K,V> {
    private K k;
    private V v;
}
