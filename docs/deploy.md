## Docker 部署

环境：**CentOS7.6**

------

### 安装Docker

确保 yum 包最新

```
sudo yum update
```

安装相关软件包

```
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```

设置yum源

```
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

安装docker

```
yum install docker-ce
```

配置阿里云镜像加速docker镜像下载

参考这个

https://www.cnblogs.com/gqzdev/p/11736856.html

启动docker

```
systemctl start docker
```

验证安装

```
docker version
```

### 创建容器

#### 启动mysql容器

拉取mysql镜像

```
docker pull centos/mysql-56-centos7
```

创建并运行容器,`123456` 是 `root` 密码。

```
docker run --name mysql56 -p 3306:3306  -e MYSQL_ROOT_PASSWORD=123456 -d centos/mysql-56-centos7
```

测试连接mysql，如：命令行或navicat，连接上即成功创建mysql容器,随后进行建库建表操作，[sql文件](https://github.com/Tyson0314/blog/blob/master/blog.sql)

#### 启动redis容器

```
docker pull redis
```

创建并运行容器：

```bash
docker run -p 6379:6379 --name redis
```

#### 启动后端容器

打包项目，jar打包在target文件夹下，`myblog-1.0.0-RELEASE.jar`

```
mvn clean package
```

编写dockerfile

```
#基础镜像jdk
FROM java:8
#挂载的路径
VOLUME /tmp
#将jar打入镜像之中
ADD myblog-1.0.0-RELEASE.jar app.jar
#容器向外暴露的端口
EXPOSE 8088
#入口命令，执行jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

将dockerfile与`myblog-1.0.0-RELEASE.jar`放在同级文件夹

```
docker build -t myblog .
```

打包完后查看一下：

```
# docker images
REPOSITORY                   TAG                 IMAGE ID            CREATED             SIZE
myblog                      latest              568f1a418c79        3 hours ago         208MB
```

启动后端容器

```
docker run --name myblog -p 8088:8088 -d myblog
```

浏览器访问centos的ip+端口，例:192.168.125.100:8088，出现如下代码，即说明启动成功。

```json
{
	"code":404,
	"message":"接口不存在",
	"data":null
}
```

#### 启动nginx容器

拉取nginx镜像

```
docker pull nginx
```

创建并启动nginx容器

```
docker run --name=nginx -p 80:80 -d docker.io/nginx
```

`docker exec -it nginx bash`进入nginx容器，切换目录`/etc/nginx/nginx.conf`，修改docker nginx配置文件：

```java
user  nginx;
worker_processes  1;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';
    
    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    #包含所有conf结尾的文件
    include /etc/nginx/conf.d/*.conf;
}
```

博客前端资源转发配置在default.conf

```
server {
    listen       80;
    server_name  localhost;
    #访问vue项目
    location / {
        root   /usr/share/nginx/html;
        index  index.html;
    }
    #将api转发到后端
    location /api/ {
        proxy_pass http://129.204.179.3:8088/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header REMOTE-HOST $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 60s;
        proxy_send_timeout 180s;
        proxy_read_timeout 180s;
        proxy_buffer_size 1M;
        proxy_buffers 8 1M;
        proxy_busy_buffers_size 1M;
        proxy_temp_file_write_size 1M;
    }
    #转发图片请求到后端
    location /img/ {
        proxy_pass http://129.204.179.3:8088/img/;
    }
}
```

管理台静态资源转发配置在blog.conf

```java
server {
    listen       8080;
    server_name  localhost;
    #访问vue项目
    location / {
        root   /usr/share/nginx/html/manage;
        index  index.html;
    }
    #将api转发到后端
    location /api/ {
        proxy_pass http://129.204.179.3:8088/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header REMOTE-HOST $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 60s;
        proxy_send_timeout 180s;
        proxy_read_timeout 180s;
        proxy_buffer_size 1M;
        proxy_buffers 8 1M;
        proxy_busy_buffers_size 1M;
        proxy_temp_file_write_size 1M;
    }
    #转发图片请求到后端
    location /img/ {
        proxy_pass http://129.204.179.3:8088/img/;
    }
}
```

前端工程下载好，执行`npm run build`打包

```
npm install 
```

打包

```
npm run build
```

将生成dist文件夹拷贝到nginx容器，博客前端资源拷贝到`/usr/share/nginx/html`，管理台前端资源拷贝到`/usr/share/nginx/html/manage`。

```
docker cp html nginx:/usr/share/nginx/html
```

重启nginx容器

```
docker restart nginx
```

打开浏览器，访问`http://129.204.179.3`可以看到博客首页，访问`http://129.204.179.3:8088`可以看到博客管理台，即docker部署完成。