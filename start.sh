#/bin/bash
base=$(cd "$(dirname "$0")";pwd)
cd $base

#Mysql地址
export MYSQL_ADDRESS=mysql:3306
#Mysql密码
export MYSQL_PASSWORD=123456
#redis地址
export REDIS_HOST=redis
#redis端口
export REDIS_PORT=6379
# smtp地址
export SMTP_HOST=
# smtp用户名
export SMTP_USERNAME=
# smtp密码
export SMTP_PASSWORD=
# 外网访问域名
export BOOKMARK_HOST=localhost
# 文件存储地址（比如用户上传的icon文件）
export BOOKMARK_FILE_SAVE_PATH=./data/files
# jwt密钥
export JWT_SECRET=123456
# http网络代理ip(github api调用可能需要)
export PROXY_IP=localhost
# http网络代理端口
export PROXY_PORT=8888
# 如果要支持github登陆需要配置以下两个参数
# github clientId 
export GITHUB_CLIENT_ID=
# github secret
export GITHUB_SECRET=
# 管理员用户id
export MANAGE_USER_ID=-1


# 前端打包
docker run -it --rm --name buildBookmark --user ${UID} -v $base/bookmark_front:/opt/front node:lts-buster-slim  bash -c "cd /opt/front &&   yarn --registry https://registry.npm.taobao.org && yarn build"
# 后端打包
docker run -it --rm --name buildBookmark --user ${UID} -v $base/data/maven/mavenRep:/var/maven/.m2: -v $base/data/maven/settings.xml:/usr/share/maven/conf/settings.xml -v $base/bookMarkService:/code maven:latest bash -c "cd /code && mvn clean install"

start="start"
stop="stop"
restart="restart"
delete="delete"

if [  -z $1 ] || [ $1 == $start ];then
  echo "start"
  docker-compose up -d
elif [ $1 == $stop ];then
  echo "stop"
  docker-compose stop 
elif [ $1 == $restart ];then
  echo "restart"
  docker-compose restart 
elif [ $1 == $delete ];then
  echo "delete"
  docker-compose down
fi
