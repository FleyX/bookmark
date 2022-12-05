本程序基于 docker 来进行部署,使用 docker-compose 管理服务。

部署过程如下：

**注意，仅在 x86 环境下测试,arm下不保证可用性（目前测试可用）**

1. 安装新版的 docker,docker-compose,zip(注意：以下操作均在项目根目录下执行)
2. 修改.env 文件中的参数,改为你的实际配置
3. 修改`浏览器插件/bookmarkBrowserPlugin/static/js/config.js`中的 bookmarkHost，改为你的实际部署路径
4. 修改`浏览器插件/bookmarkBrowserPlugin/tab/index.html`中的`<meta http-equiv="Refresh" content="0;url=https://bm.fleyx.com" />`，将 url 改为你的实际部署地址
5. 执行`build.sh`编译前后端代码
6. root 权限运行 `docker-compose up -d` 启动服务。
