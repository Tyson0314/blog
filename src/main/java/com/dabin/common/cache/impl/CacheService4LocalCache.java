package com.dabin.common.cache.impl;

import com.dabin.common.cache.CacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * guava 缓存实现
 *
 * @author 大彬
 * @date 2021-07-18 23:01
 **/
@Service
public class CacheService4LocalCache implements CacheService {

    private static Cache<String, Long> commentCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(100000L).build();
    private long COMMENT_LIMIT_PER_HOUR = 5;

    @Override
    public boolean isValidComment(String ip) {
        if (StringUtils.isNullOrEmpty(ip)) {
            return false;
        }
        Long result = commentCache.getIfPresent(ip);
        if (Objects.isNull(result)) {
            commentCache.put(ip, 1L);
        } else if (result < COMMENT_LIMIT_PER_HOUR) {
            commentCache.put(ip, ++result);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidViewOrLike(String ip) {
        if (StringUtils.isNullOrEmpty(ip) || Objects.nonNull(commentCache.getIfPresent(ip))) {
            return false;
        }
        commentCache.put(ip, 1L);
        return true;
    }
}
