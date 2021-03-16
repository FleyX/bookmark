本程序基于 docker 来进行部署,使用docker-compose管理服务。

部署过程如下：

**注意，仅在x86环境下测试，不保证能在arm环境下使用**

1. 安装新版的docker和docker-compose
2. 修改start.sh中的参数
3. root权限（或者可操作docker的普通用户）运行 `start.sh` 启动服务。