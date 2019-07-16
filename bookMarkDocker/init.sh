#/bin/bash

# 用于前后端打包
docker run -it --rm --name buildBookmark -v ../front:/opt/front -v ../bookMarkService:/opt/backend registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v1 sh -c "cd /opt/front && npm --registry https://registry.npm.taobao.org install && npm run build && cd /opt/backend && mvn package"

echo "打包完毕，export MYSQL_PASS=xxx 设置环境变量后执行docker-compose up -d即可"