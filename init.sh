#/bin/bash
base=$(cd "$(dirname "$0")";pwd)

cd $bash

# 用于前后端打包

docker run -t --rm --name buildBookmark -v $base/front:/opt/front node:lts-buster-slim  bash -c "cd /opt/front && npm --registry https://registry.npm.taobao.org install && npm run build"

docker run -t --rm --name buildBookmark  -v $base/data/maven/mavenRep:/root/.m2 -v $base/data/maven/settings.xml:/usr/share/maven/conf/settings.xml -v $base/bookMarkService:/code maven:latest bash -c "cd /code && mvn clean install"

#docker-compose up
