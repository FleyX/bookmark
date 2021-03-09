#/bin/bash
base=$(cd "$(dirname "$0")";pwd)
cd $base

#Mysql地址
export MYSQL_ADDRESS=localhost:3306
#Mysql密码
export MYSQL_PASSWORD=123456
#redis地址
export REDIS_HOST=localhost
#redis端口
export REDIS_PORT=6379
# smtp地址
export SMTP_HOST=
# smtp用户名
export SMTP_USERNAME=
# smtp密码
export SMTP_PASSWORD=
# 部署后前端访问地址
export BOOKMARK_HOST=localhost
# 文件存储地址
export BOOKMARK_FILE_SAVE_PATH=./data/files
# jwt密钥
export JWT_SECRET=123456

# 前端打包
docker run -it --rm --name buildBookmark -v $base/bookmark_front:/opt/front node:lts-buster-slim  bash -c "cd /opt/front &&   yarn --registry https://registry.npm.taobao.org && yarn build"
# 后端打包
docker run -it --rm --name buildBookmark  -v $base/data/maven/mavenRep:/root/.m2 -v $base/data/maven/settings.xml:/usr/share/maven/conf/settings.xml -v $base/bookMarkService:/code maven:latest bash -c "cd /code && mvn clean install"

docker-compose up -d
