package com.dabin.common.exception;

import com.dabin.common.base.Result;
import com.dabin.common.constants.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * 全局异常处理
 *
 * @author 大彬
 * @datetime 2021/7/2 9:40
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String logExceptionFormat = "Capture Exception By GlobalExceptionHandler: Code: %s Detail: %s";

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Boolean> runtimeExceptionHandler(RuntimeException ex) {
        return resultFormat(1, ex);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Boolean> nullPointerExceptionHandler(NullPointerException ex) {
        System.err.println("NullPointerException:");
        return resultFormat(2, ex);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public Result<Boolean> classCastExceptionHandler(ClassCastException ex) {
        return resultFormat(3, ex);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public Result<Boolean> iOExceptionHandler(IOException ex) {
        return resultFormat(4, ex);
    }

    /**
     * 未知方法异常
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public Result<Boolean> noSuchMethodExceptionHandler(NoSuchMethodException ex) {
        return resultFormat(5, ex);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Result<Boolean> indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {
        return resultFormat(6, ex);
    }

    /**
     * 400错误
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Result<Boolean> requestNotReadable(HttpMessageNotReadableException ex) {
        log.error("400..requestNotReadable");
        return resultFormat(7, ex);
    }

    /**
     * 400错误
     */
    @ExceptionHandler({TypeMismatchException.class})
    public Result<Boolean> requestTypeMismatch(TypeMismatchException ex) {
        log.error("400..TypeMismatchException");
        return resultFormat(8, ex);
    }

    /**
     * 400错误
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Result<Boolean> requestMissingServletRequest(MissingServletRequestParameterException ex) {
        log.error("400..MissingServletRequest");
        return resultFormat(9, ex);
    }

    /**
     * 405错误
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result<Boolean> request405(HttpRequestMethodNotSupportedException ex) {
        return resultFormat(10, ex);
    }

    /**
     * 406错误
     */
    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    public Result<Boolean> request406(HttpMediaTypeNotAcceptableException ex) {
        log.error("406...");
        return resultFormat(11, ex);
    }

    /**
     * 500错误
     */
    @ExceptionHandler({ConversionNotSupportedException.class, HttpMessageNotWritableException.class})
    public Result<Boolean> server500(RuntimeException ex) {
        log.error("500...");
        return resultFormat(12, ex);
    }

    /**
     * 栈溢出
     */
    @ExceptionHandler({StackOverflowError.class})
    public Result<Boolean> requestStackOverflow(StackOverflowError ex) {
        return resultFormat(13, ex);
    }

    /**
     * 除数不能为0
     */
    @ExceptionHandler({ArithmeticException.class})
    public Result<Boolean> arithmeticException(ArithmeticException ex) {
        return resultFormat(13, ex);
    }


    /**
     * 其他错误
     */
    @ExceptionHandler({Exception.class})
    public Result<Boolean> exception(Exception ex) {
        return resultFormat(14, ex);
    }

    private <T extends Throwable> Result<Boolean> resultFormat(Integer code, T ex) {
        ex.printStackTrace();
        log.error(String.format(logExceptionFormat, code, ex.getMessage()));
        return Result.createByErrorCodeMessage(ResultCode.SERVER_ERROR.getCode(), "服务端异常");
    }
}
