package com.dabin.controller.manage;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.common.security.SecurityUserDetail;
import com.dabin.dto.UserLoginRequest;
import com.dabin.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员登录controller
 *
 * @author 大彬
 * @datetime 2021/8/7 15:34
 **/
@RestController
@RequestMapping
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation(value ="登录")
    public Result<String> login(@RequestBody UserLoginRequest userLoginRequest){
        validate(userLoginRequest);

        return userService.login(userLoginRequest);
    }
    @GetMapping("/logout")
    @ApiOperation(value ="登出")
    public Result<Boolean> logout(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = new Cookie("X-Token", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return Result.createBySuccess(Boolean.TRUE);
    }


    @GetMapping("/manage/system/user")
    public Result<SecurityUserDetail> getUserInfo(){
        SecurityUserDetail securityUserDetail = new SecurityUserDetail();
        securityUserDetail.setId(1L);
        securityUserDetail.setUserName("大彬");
        securityUserDetail.setRole("ROLE_ADMIN");

        return Result.createBySuccess(securityUserDetail);
    }

}
