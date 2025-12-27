#!/bin/bash
base=$(cd "$(dirname "$0")";pwd)
echo $base
cd $base

cd 浏览器插件/bookmarkBrowserPlugin
zip -q -r ../../bookmark_front/public/static/bookmarkBrowserPlugin.zip *

cd ../../
# 前端打包
docker run  --rm --user ${UID} -v $base/bookmark_front:/opt/front node:18-slim  bash -c "cd /opt/front &&   npm install -g pnpm && pnpm install && pnpm build"
# 后端打包
docker run  --rm --user ${UID} -v $base/data/maven/mavenRep:/var/maven/.m2 -v $base/data/maven/settings.xml:/usr/share/maven/conf/settings.xml -v $base/bookMarkService:/code maven:3-eclipse-temurin-25-alpine  bash -c "cd /code && mvn clean install"