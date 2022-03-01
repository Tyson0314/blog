package com.dabin.common.base;

import com.dabin.common.cache.impl.CacheService4LocalCache;
import com.dabin.common.constants.ResultCode;
import com.dabin.common.exception.BusinessException;
import com.dabin.dto.PageInfo4Request;
import com.dabin.entity.BaseEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Preconditions;
import com.mysql.cj.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * service的基类，封装通用代码，减少重复代码
 *
 * @author 大彬
 * @date 2021-07-02 22:36
 */
public class BaseService {

    @Resource
    private CacheService4LocalCache cacheService4LocalCache;

    protected boolean isValidViewOrLike(String targetId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip =request.getHeader("X-Real-Ip") + "";
        if (StringUtils.isNullOrEmpty(targetId)) {
            return Boolean.FALSE;
        }
        return cacheService4LocalCache.isValidViewOrLike(String.join("_", ip, targetId));
    }

    /**
     * 分页信息
     */
    public Page getPage(PageInfo4Request pageInfo4Request){
        if (Objects.isNull(pageInfo4Request)) {
            pageInfo4Request = new PageInfo4Request();
        }
        Page page = PageHelper.startPage(pageInfo4Request.getPageNum(), pageInfo4Request.getPageSize());
        page.setOrderBy(pageInfo4Request.getOrderBy());
        return page;
    }


    /**
     * 成功相应的结果集
     */
    protected Result resultOk() {
        return resultOk(Boolean.TRUE);
    }

    protected <T> Result resultOk(T data) {
        return Result.createBySuccess(data);
    }

    /**
     * 有问题的响应
     *
     * @return
     */
    protected Result resultError() {
        return resultError(ResultCode.UNKNOWN.getCode(), ResultCode.UNKNOWN.getDescription());
    }

    protected Result resultError(Integer errorCode, String msg) {
        return Result.createByErrorCodeMessage(errorCode, msg);
    }


    protected Result resultError4Param(String msg) {
        return resultError(ResultCode.PARAMAS_ERROR.getCode(), msg);
    }

    /**
     * 数据库异常
     *
     * @return
     */
    protected Result resultError4DB() {
        return resultError(ResultCode.DATABASE_ERROR.getCode(), ResultCode.DATABASE_ERROR.getDescription());
    }

    protected Result resultError4DB(String msg) {
        return resultError(ResultCode.DATABASE_ERROR.getCode(), msg);
    }

    protected void fillCreateTime(BaseEntity baseEntity){
        Preconditions.checkNotNull(baseEntity, "创建的类不能为nul");
        baseEntity.setCreateTime(new Date());
    }
    protected void fillUpdateTime(BaseEntity baseEntity){
        Preconditions.checkNotNull(baseEntity, "要修改的类不能为nul");
        baseEntity.setUpdateTime(new Date());
    }

    /**
     * 判断对象是否存在，不存在抛出异常
     * @param obj
     */
    public void checkExists(Object obj){
        if (Objects.isNull(obj)) {
            throw new BusinessException("数据不存在");
        }
    }
}
