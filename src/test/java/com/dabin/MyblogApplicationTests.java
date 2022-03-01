package com.dabin;

import com.dabin.common.utils.AESCbcUtils;
import com.dabin.common.utils.FileUtils;
import com.dabin.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-dev.yml")
@Slf4j
public class MyblogApplicationTests {

    @Autowired
    FileUtils fileUtils;

    @Test
    public void aesTest() {
         try {
             String hexKey = "d75bd94d434b58bcb02d30efe460cf67";
             AESCbcUtils.genSecretKey();
             String hexIv = "c9e5a4d463721b488330dd49ea645565";
             AESCbcUtils.genHexIv();
             System.out.println(hexIv);
             String randomString = "9DFDC027-D7FD-774A-A2D8-AB1F9A13D259哈哈";
             System.out.println("1. 原文: " + randomString);
             String cipherText = AESCbcUtils.encrypt(hexKey, hexIv, randomString);
             System.out.println("2. 密文: " + cipherText);
             String decryptedString = AESCbcUtils.decrypt(hexKey, hexIv, cipherText);
             System.out.println("3. 解密文 : " + decryptedString);
         } catch (Exception e) {
             System.out.println("aesTest error, " + e);
         }
     }

     @Test
     public void testQiniu() {
         FileVO fileVO = new FileVO();
         fileVO.setUserId("大彬");
         fileVO.setUrl("https://gitee.com/tysondai/img/raw/master/image-20211230084521440.png");

         System.out.println(fileUtils.uploadPicture(fileVO));
     }

}
