package com.dabin.service;

import com.dabin.common.base.Result;
import me.zhyd.oauth.model.AuthCallback;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author: 程序员大彬
 * @time: 2021-12-11 19:49
 */
public interface AuthService {
    /**
     * 认证
     */
    public Result<Map<String, Object>> verify(String token);

    /**
     * 回调
     */
    public void callback(String source, AuthCallback callback, HttpServletResponse httpServletResponse) throws IOException;

}
