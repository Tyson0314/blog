package com.dabin.controller;

import com.dabin.common.base.Result;
import com.dabin.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 生成七牛云token
 *
 * @author 大彬
 * @datetime 2021/7/10 17:34
 **/
@RestController
@RequestMapping("/upload/token")
public class QiNiuTokenController {
    @Resource
    private CommonService commonService;
    @GetMapping
    public Result<String> generateUploadToken(){

        return commonService.getQiniuUploadToken();
    }

}
