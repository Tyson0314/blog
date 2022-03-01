package com.dabin.common.cache;

/**
 * 缓存抽象类
 *
 * @author 大彬
 * @date 2021-07-17 22:38
 **/
public interface CacheService<T> {

    /**
     * 限制留言
     * @param
     */
    boolean isValidComment(String key);

    /**
     * 限制访问统计和点赞
     * @param key
     */
    boolean isValidViewOrLike(String key);
}
