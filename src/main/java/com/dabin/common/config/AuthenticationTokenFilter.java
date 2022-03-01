package com.dabin.common.config;

import com.dabin.common.constants.RedisConstant;
import com.dabin.common.utils.RedisUtils;
import com.dabin.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.dabin.common.constants.SystemConstant.*;

/**
 * @author: 程序员大彬
 * @time: 2021-11-21 16:38
 */
@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        //得到请求头信息authorization信息
        String accessToken = request.getHeader("Authorization");

        log.info("accessToken:{}", accessToken);

        if (accessToken != null) {
            //从Redis中获取内容
            User user = redisUtils.get(RedisConstant.TOKEN + accessToken, User.class);
            if (user != null) {
                log.info("访问用户:{}", user);
                //把userUid存储到 request中
                request.setAttribute(ACCESS_TOKEN, accessToken);
                request.setAttribute(USER_ID, user.getId());
                request.setAttribute(USER_NAME, user.getNickName());
                log.info(request.getAttribute(USER_ID).toString());
            }
        }
        chain.doFilter(request, response);
    }
}
