package com.dabin.common.exception;

/**
 * @description 自定义业务异常
 *
 * @author 大彬
 * @date 2021-07-02 22:06
 **/
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 4947743645170712257L;

    private int code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
