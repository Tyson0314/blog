package com.dabin.common.utils;

import com.dabin.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author: 程序员大彬
 * @time: 2022-01-11 00:48
 */
@Slf4j
@Component
public class FileUtils {

    @Autowired
    QiniuUtils qiniuUtils;

    public String uploadPicture(FileVO fileVO) {
        if (fileVO == null) {
            log.error("file is null!");
            return null;
        }

        if (StringUtils.isEmpty(fileVO.getUserId())) {
            log.error("不存在的用户，请先注册!");
            return null;
        }

        String fileUrl = fileVO.getUrl();

        if (StringUtils.isNotEmpty(fileUrl)) {
            java.io.File dest = null;
            // 将图片上传到本地服务器中以及七牛云中
            BufferedOutputStream out = null;
            FileOutputStream os = null;
            // 输入流
            InputStream inputStream = null;
            //获取新文件名 【默认为jpg】
            String newFileName = System.currentTimeMillis() + ".jpg";
            try {
                // 构造URL
                URL url = new URL(fileUrl);
                // 打开连接
                URLConnection con = url.openConnection();
                // 设置用户代理
                con.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
                // 设置10秒
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                // 当获取的相片无法正常显示的时候，需要给一个默认图片
                inputStream = con.getInputStream();
                // 1K的数据缓冲
                byte[] bs = new byte[1024];
                // 读取到的数据长度
                int len;
                String tempFiles = "temp/" + newFileName;
                dest = new java.io.File(tempFiles);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                os = new FileOutputStream(dest, true);
                // 开始读取
                while ((len = inputStream.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                FileInputStream fileInputStream = new FileInputStream(dest);
                MultipartFile fileData = new MockMultipartFile(dest.getName(), dest.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
                out = new BufferedOutputStream(new FileOutputStream(dest));
                out.write(fileData.getBytes());

                out.flush();
                out.close();
                return qiniuUtils.uploadQiniu(dest);
            } catch (Exception e) {
                log.error("upload picture fail:", e);
            } finally {
                if (dest != null && dest.getParentFile().exists()) {
                    dest.delete();
                }
            }
        }

        return null;
    }
}
