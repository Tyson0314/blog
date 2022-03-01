package com.dabin.controller;

import com.dabin.common.base.BaseController;
import com.dabin.common.base.Result;
import com.dabin.entity.User;
import com.dabin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 程序员大彬
 * @time: 2021-11-14 14:21
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @PostMapping("/edit")
    public Result edit(@RequestBody User user) {
        return userService.edit(user);
    }
}
