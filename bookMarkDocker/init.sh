#/bin/bash
base=$(cd "$(dirname "$0")";pwd)

# 创建es存放数据文件夹并设置读写权限
# mkdir $base/es/data>/dev/null 2>&1

#chmod 777 $base/es/data

#sysctl -w vm.max_map_count=262144>/dev/null 2>&1

# 用于前后端打包

docker run -it --rm --name buildBookmark -v $base/../front:/opt/front registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v2 sh -c "cd /opt/front && npm --registry https://registry.npm.taobao.org install && npm run build"

docker run -it --rm --name buildBookmark  -v $base/mavenRep:/opt/mavenRep -v $base/../bookMarkService:/opt/backend  registry.cn-hangzhou.aliyuncs.com/fleyx/node-java-env:v2 sh -c "cd /opt/backend && mvn clean install && echo over"
