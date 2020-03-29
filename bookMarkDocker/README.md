本程序基于 docker 来进行部署。

docker 镜像 构建文件为本目录下的`Dockerfile`,已经生产推送到阿里云的容器镜像库：registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v2.本镜像包含如下：

- node 运行环境，已安装 cnpm
- java 运行编译环境，openjdk11
- maven 运行环境，已设置为阿里源

部署过程如下：

1. 首先root运行 init.sh 进行环境初始化以及前后端打包。
2. 将密码，smtp 等相关敏感信息设置 到环境变量中,内容如下：<br/>

   ```bash
   export MYSQL_PASSWORD=123456
   export JWT_SECRET=123456
   export SMTP_HOST=localhost
   export SMTP_USERNAME=test
   export SMTP_PASSWORD=test
   export SMTP_PORT=465
   # 文件存储路径
   export BOOKMARK_FILE_SAVE_PATH=./fileSave
   # 服务部署地址
   export BOOKMARK_HOST=http://localhost:8080
   ```

   两种设置办法：

   - 在终端执行上述命令.这种办法在关闭终端后这些变量会失效，如果重新部署 docker-compose 会报警告--环境变量未定义

   - 写到配置文件中，比如/etc/profile 等文件中,然后`source /etc/profile` 使其生效。

3. 执行`docker-compose up -d` 后台启动系统。
