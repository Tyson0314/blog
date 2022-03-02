# 简介

本项目基于[开源项目](https://github.com/liqianggh/blog)二次开发，后端技术栈为SpringBoot + mybatis + MySQL + Redis开发，前端使用vue，前后端分离。

[博客前端项目地址](https://github.com/Tyson0314/blog-web)

[博客管理台前端项目地址](https://github.com/Tyson0314/blog-web-manage)

[博客预览地址（首次加载有点慢）](http://dabin-coder.cn)

[博客管理台预览地址](http://dabin-coder.cn:8080)

博客截图：

![](https://gitee.com/tysondai/img/raw/master/image-20220301223342568.png)

![](https://gitee.com/tysondai/img/raw/master/image-20220301223515502.png)

![](https://gitee.com/tysondai/img/raw/master/image-20220301223551334.png)

# 开发指南

## 运行

1. maven 项目 需要maven环境。

2. 导入sql/blog.sql脚本。

2. 设置redis和MySQL密码。

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


# TODO

- 评论、点赞消息通知
- qq/wechat登录
- 子评论功能
- ...

#  贡献代码

开源项目离不开大家的支持，如果您有好的想法，或者修复了BUG，欢迎小伙伴们提交 **Pull Request** 参与开源贡献。

1. **fork** 本项目到自己的代码仓
2. 把 **fork** 过去的项目也就是你仓库中的项目 **clone** 到你的本地
3. 新建分支
4. 修改代码
5. **commit** 后 **push** 到自己的库
6. 发起**PR**（ **pull request**） 请求，提交到 **feature** 分支
7. 等待合并
