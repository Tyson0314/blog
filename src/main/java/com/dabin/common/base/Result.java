package com.dabin.common.base;

import com.dabin.common.constants.ResultCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author 大彬
 * @description 接口返回结果包装类
 * @date 2021-11-17 19:24
 */
public class Result<T> implements Serializable {
    /**
     * 状态码
     */
    private int status;

    /**
     * 消息，状态码的相关描述
     */
    private String msg;

    /**
     * 服务端响应的数据
     */
    private T data;

    private Result(int status, String msg) {
        this(status, null, msg);
    }

    private Result(int status, T data, String msg) {
        this.data = data;
        this.status = status;
        this.msg = msg;
    }

    /**
     * 成功消息体构建
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> createBySuccess() {
        return createBySuccess(null, null);
    }

    public static <T> Result<T> createBySuccess(T data) {
        return createBySuccess(null, data);
    }

    public static <T> Result<T> createBySuccess(String msg, T data) {
        return new Result<T>(ResultCode.NORMAL_RETURNED.getCode(), data, msg);
    }

    /**
     * 失败消息体构建
     *
     * @param <T>
     * @return
     */
    public static <T> Result<T> createByError() {
        return createByErrorMessage(ResultCode.UNKNOWN.getDescription());
    }

    public static <T> Result<T> createByErrorMessage(String errorMsg) {
        return createByErrorCodeMessage(ResultCode.UNKNOWN.getCode(), errorMsg);
    }

    public static <T> Result<T> createByErrorCodeMessage(int errorCode, String errorMsg) {
        return new Result<T>(errorCode, errorMsg);
    }

    public static <T> Result<T> createByErrorCode(ResultCode resultCode) {
        return createByErrorCodeMessage(resultCode.getCode(), resultCode.getDescription());
    }

    /**
     * 序列化时忽略
     */
    @JsonIgnore
    public boolean isOk() {
        return this.status == ResultCode.NORMAL_RETURNED.getCode();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "{" + "status: " + status + ", msg:'" + msg + '\'' + ", data:" + data + '}';
    }
}
