package com.dabin.service;

import com.dabin.common.base.Result;

public interface CommonService {

    /**
     * 生成token
     */
    public Result<String> getQiniuUploadToken();

    /**
     * 获取redis key
     */
    String getVoteKey(String type, int blogId, String userId);
}
