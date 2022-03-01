package com.dabin.common.base;

import com.dabin.common.constants.ResultCode;
import com.dabin.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 所有Controller继承的基类Controller
 *
 * @author 大彬
 * @date 2021-07-02 22:05
 */
@Slf4j
public class BaseController {

    @Autowired(required = false)
    Validator validator;

    public void validate(Object params) {
        if (validator == null) {
            log.warn("没有可用的validator");
            return;
        }
        Set<ConstraintViolation<Object>> validate = this.validator.validate(params);
        for (ConstraintViolation<Object> objectConstraintViolation : validate) {
            throw new BusinessException(ResultCode.PARAMAS_ERROR.getCode(), objectConstraintViolation.getMessage());
        }
    }

    /**
     * 获取HttpServletRequest
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse
     */
    protected HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

}
