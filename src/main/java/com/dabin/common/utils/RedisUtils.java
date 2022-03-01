package com.dabin.common.utils;

import com.dabin.common.constants.RedisConstant;
import com.dabin.common.constants.VoteType;
import com.dabin.entity.Blog;
import com.dabin.entity.Comment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: 程序员大彬
 * @time: 2021-11-13 00:06
 */
@Component
@Slf4j
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ValueOperations<String, String> valueOperations;
    /**
     * 默认过期时长 1天，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;
    private final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public void set(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 从Redis获取博文点赞数
     */
    public List<Blog> getBlogVoteCountFromRedis() {
        List<Blog> blogList = new ArrayList<>();
        try {
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisConstant.MAP_VOTE_COUNT_KEY, ScanOptions.NONE);
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();

                String key = (String) entry.getKey();
                String[] keyArr = key.split(RedisConstant.VOTE_KEY_SPLIT);

                if (keyArr == null || keyArr.length < 2) {
                    log.warn("parse blog vote count key error, key {}", key);
                }
                String type = keyArr[0];

                if (StringUtils.equals(type, VoteType.ARTICLE)) {
                    int blogId = Integer.parseInt(keyArr[1]);
                    int voteCount = Integer.parseInt((String) entry.getValue());

                    Blog blog = new Blog();
                    blog.setId(blogId);
                    blog.setVoteCount(voteCount);

                    blogList.add(blog);
                    //从redis删除key
                    redisTemplate.opsForHash().delete(RedisConstant.MAP_VOTE_COUNT_KEY, key);
                }
            }
            cursor.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return blogList;
    }

    /**
     * 从Redis获取博文点赞数
     */
    public List<Comment> getCommentVoteCountFromRedis() {
        List<Comment> commentList = new ArrayList<>();
        try {
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(RedisConstant.MAP_VOTE_COUNT_KEY, ScanOptions.NONE);
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();

                String key = (String) entry.getKey();
                String[] keyArr = key.split(RedisConstant.VOTE_KEY_SPLIT);

                if (keyArr == null || keyArr.length < 2) {
                    log.warn("parse comment vote count key error, key {}", key);
                }
                String type = keyArr[0];

                if (StringUtils.equals(type, VoteType.COMMENT)) {
                    int commentId = Integer.parseInt(keyArr[1]);
                    int voteCount = Integer.parseInt((String) entry.getValue());

                    Comment c = new Comment();
                    c.setId(commentId);
                    c.setVoteCount(voteCount);

                    commentList.add(c);
                    //从redis删除key
                    redisTemplate.opsForHash().delete(RedisConstant.MAP_VOTE_COUNT_KEY, key);
                }
            }
            cursor.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return commentList;
    }
}

