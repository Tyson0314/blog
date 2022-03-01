package com.dabin.service.impl;

import com.dabin.common.base.BaseService;
import com.dabin.common.base.Result;
import com.dabin.common.constants.RedisConstant;
import com.dabin.service.CommonService;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 公共servcie
 *
 * @author 大彬
 * @datetime 2021/7/10 17:36
 **/
@Slf4j
@Service
public class CommonServiceImpl extends BaseService implements CommonService {

    @Value(value = "${qiniu.accessKey}")
    private String accessKey;

    @Value(value = "${qiniu.secretKey}")
    private String secretKey;

    @Value(value = "${qiniu.bucket}")
    private String bucket;

    /**
     * 生成token
     * @return
     */
    @Override
    public Result<String> getQiniuUploadToken() {

        Auth auth = Auth.create(accessKey, secretKey);
        log.info("accesskey:{}, secretKey:{}", accessKey, secretKey);
        return resultOk(auth.uploadToken(bucket));
    }

    @Override
    public String getVoteKey(String type, int blogId, String userId) {
        StringBuilder builder = new StringBuilder();
        builder.append(type);
        builder.append(RedisConstant.VOTE_KEY_SPLIT);
        builder.append(blogId);
        builder.append(RedisConstant.VOTE_KEY_SPLIT);
        builder.append(userId);

        return builder.toString();
    }
}
