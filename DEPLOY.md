本程序基于 docker 来进行部署,使用 docker-compose 管理服务。

**注意，仅在 x86 环境下测试,arm 下不保证可用性（目前测试可用）**

## 首次部署

0. 克隆代码`git clone https://github.com/FleyX/bookmark.git`
1. 进入文件夹`cd bookmark`
2. 安装新版的 docker,docker-compose,zip `apt install docker docker-compose zip`
3. 修改.env 文件中的参数,改为你的实际配置
4. 修改`浏览器插件/bookmarkBrowserPlugin/static/js/config.js`中的 bookmarkHost，改为你的实际部署路径
5. 修改`浏览器插件/bookmarkBrowserPlugin/tab/index.html`中的`<meta http-equiv="Refresh" content="0;url=https://bm.fleyx.com" />`，将 url 改为你的实际部署地址
6. 执行`build.sh`编译前后端代码 `bash build.sh`
7. root 权限运行 `docker-compose up -d` 启动服务。

## 更新系统

0. 代码库更新`cd bookmark;git pull`
1. 执行`build.sh`编译前后端代码 `bash build.sh`
2. root 权限运行 `docker-compose restart` 启动服务
