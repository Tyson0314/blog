package com.dabin.service;

import com.dabin.common.base.Result;
import com.dabin.dto.UserLoginRequest;
import com.dabin.entity.User;

public interface UserService {

    /**
     * 登录
     */
    public Result<String> login(UserLoginRequest userLoginRequest);

    /**
     * 编辑
     */
    public Result edit(User user);

    /**
     * 新增用户
     */
    public Result<Boolean> createUserInfo(User user);

    /**
     * 更新用户信息
     */
    public Result<Boolean> updateVisitorInfo(User user);

    /**
     * 查询用户
     */
    public User getUserById(String userId);

    /**
     * 根据邮箱查询
     */
    public User getUserDOByEmail(String visitorEmail);
}
