# blog
个人博客，基于SpringBoot + mybatis + MySQL + Redis开发

# 开发指南

## 运行

1. maven 项目 需要maven环境。

2. 导入sql/blog.sql脚本。

3. 配置七牛云图床参数。

   ```java
   qiniu:
     accessKey: 
     secretKey:
     bucket: 
     url-prefix: 
   ```

4. 配置第三方登录功能相关参数。详情见第三方登录功能部分。

   ```java
   justAuth:
     clientId:
       gitee: 
       github: 
     clientSecret:
       gitee: 
       github: 
   ```

5. 运行com/dabin/MyblogApplication.java

## 生成mapper文件

此项目使用mybatis-generator生成通用mapper文件，操作步骤如下：

1、resource/mybatis-generator-config.xml，配置需要生成mapper文件的表，比如comment表。

```xml
<table tableName="comment" domainObjectName="Comment" enableCountByExample="true" enableDeleteByExample="true"
       enableSelectByExample="true" enableUpdateByExample="true" enableInsert="true"></table>
```

2、运行mybatis-generator插件，即可生成通用mapper文件。

![](https://gitee.com/tysondai/img/raw/master/image-20220301002441621.png)

mybatis-generator详细配置可以参考：https://juejin.cn/post/6844903982582743048

## 自定义mapper

自定义mapper文件放到resource/mappers/custom目录下。

## 部署

[见部署文档](./docs/deploy.md)

## 第三方登录功能

[开箱即用的整合第三方登录的开源组件](https://justauth.wiki/)


# todo

- 评论、点赞消息通知
- qq/wechat登录
- 子评论功能
- ...

