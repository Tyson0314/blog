package com.dabin.common.utils;

/**
 * @author: 程序员大彬
 * @time: 2022-01-11 23:25
 */

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

/**
 * 七牛云工具类
 *
 * @author 大彬
 * @date 2022年1月15日
 */
@Slf4j
@Component
public class QiniuUtils {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.url-prefix}")
    private String prefix;

    /**
     * 七牛云上传图片
     *
     * @param localFilePath
     * @return
     */
    public String uploadQiniu(File localFilePath) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //生成上传凭证，然后准备上传
        UploadManager uploadManager = new UploadManager(cfg);
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        log.info("upToken:{}", upToken);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("{七牛图片上传key: " + putRet.key + ",七牛图片上传hash: " + putRet.hash + "}");
            return prefix + putRet.key;
        }
         catch (QiniuException ex) {
            log.error("qiniu upload fail, error", ex);
        }
        return null;
    }
}
