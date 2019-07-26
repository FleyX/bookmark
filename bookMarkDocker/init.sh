#/bin/bash
base=$(cd "$(dirname "$0")";pwd)
echo $base

# 用于前后端打包

docker run -it --rm --name buildBookmark -v $base/../front:/opt/front registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v1 sh -c "cd /opt/front && npm --registry https://registry.npm.taobao.org install && npm run build"

docker run -it --rm --name buildBookmark  -v $base/mavenRep:/opt/mavenRep -v $base/../bookMarkService:/opt/backend -e MAVEN_OPTS="-Xms256m -Xmx512m -Dfile.encoding=UTF-8" registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v1 sh -c "cd /opt/backend && mvn clean install && echo over"

echo "打包完毕，export MYSQL_PASS=xxx 设置环境变量后执行docker-compose up -d即可"
