package com.dabin.controller;

import com.alibaba.fastjson.JSONObject;
import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.common.constants.RedisConstant;
import com.dabin.common.utils.RedisUtils;
import com.dabin.service.impl.AuthServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: 程序员大彬
 * @time: 2021-11-06 16:27
 */
@RestController
@RequestMapping("/oauth")
@Api(value = "第三方登录相关接口", tags = {"第三方登录相关接口"})
@Slf4j
public class AuthController extends BaseController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AuthServiceImpl authService;


    @ApiOperation(value = "获取认证", notes = "获取认证")
    @RequestMapping("/render/{source}")
    public Result<Map<String, String>> renderAuth(@PathVariable("source") String source, HttpServletResponse response) throws IOException {
        log.info("进入render:" + source);
        AuthRequest authRequest = authService.getAuthRequest(source);
        String token = AuthStateUtils.createState();
        String authorizeUrl = authRequest.authorize(token);
        Map<String, String> map = new HashMap<>();
        map.put("url", authorizeUrl);
        return Result.createBySuccess(map);
    }

    @RequestMapping("/callback/{source}")
    public void login(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse httpServletResponse) throws IOException {
        log.info("进入callback：" + source + "   ：" + JSONObject.toJSONString(callback));
        authService.callback(source, callback, httpServletResponse);
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @GetMapping("/verify/{accessToken}")
    public Result<Map<String, Object>> verifyUser(@PathVariable("accessToken") String accessToken) {
        return authService.verify(accessToken);
    }

    @RequestMapping("/delete/{accessToken}")
    public Result<String> deleteUserAccessToken(@PathVariable("accessToken") String accessToken) {
        log.info("deleteUserAccessToken:{}", accessToken);
        redisUtils.delete(RedisConstant.TOKEN + accessToken);
        return Result.createBySuccess("删除token成功");
    }

}
