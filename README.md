![图片](https://s3.fleyx.com/picbed/2023/08/Snipaste_2023-08-13_15-33-16.png)

本项目是一个在线书签管理的项目，名为：签签世界.

在线使用地址(长期提供服务):[bm.fleyx.com](https://bm.fleyx.com)，

**为获得更好的体验，建议将主页设置为 bm.fleyx.com,并安装浏览器拓展,[点击查看如何安装](https://blog.fleyx.com/blog/detail/20220329/#%e6%b5%8f%e8%a7%88%e5%99%a8%e6%8f%92%e4%bb%b6)**

也可自己部署搭建，教程见：[docker-compose 部署](https://github.com/FleyX/bookmark/blob/master/DEPLOY.md)

# 缘由

1. 主要用的是 chrome，但是有时候需要用其他的浏览器：Firefox，ie 等。然后这些浏览器上没有书签，想进个网站还得打开 chrome 复制 url，太麻烦。

2. chrome 必须翻墙才能同步书签，体验不是那么好。

3. 如果书签全放在 chrome 上，相当于绑定死 chrome 浏览器了，很难迁移到别的优秀浏览器，比如 firfox 上。

所以有了这样这样一个项目，建立一个和平台无关的书签管理器，可在任意平台使用。

# 主要功能

帮助文档：[点击跳转](https://blog.fleyx.com/blog/detail/20220329/)

- 支持从 chrome,edge,firefox 等浏览器导入书签数据。
- 支持从 OneEnv 导入书签数据
- 树型多级目录支持
- 支持导出标准 html 书签文件
- 强大的检索功能，支持拼音检索
- 支持浏览器插件，安装插件以后可右键添加书签

# 管理员与权限

- **超级管理员**：系统首个注册用户自动成为超级管理员（`super_admin`），拥有全部权限。存量部署升级时，数据库迁移会自动将最早注册的用户绑定为超级管理员。
- **权限模型**：角色-权限-资源。权限与资源（前端页面 `VIEW:` 资源与后端接口资源）统一定义在后端 `bookMarkService/web/src/main/resources/permissions.yml`；角色及其权限绑定关系在管理后台维护，一个用户可绑定多个角色，权限取并集。
- **管理后台**：首页底部"管理后台"链接（仅登录且拥有任一 `VIEW:/admin/*` 资源的用户可见）可进入，包含用户管理（禁用/启用、分配角色、重置密码）与角色管理。
- **开发注意**：新增受控接口必须在 `permissions.yml` 中登记资源，否则除超级管理员外的用户访问会返回 403。

# 更新日志

## 1.4.1

- 修复书签名过长无法导入问题

## 1.4

- 优化首图加载逻辑
- 支持 OneEnv 备份文件导入

## 1.3

![pic](https://s3.fleyx.com/picbed/2023/08/Snipaste_2023-08-13_15-01-20.png)

搜索引擎支持自定义[#43](https://github.com/FleyX/bookmark/issues/43)

位置：右上角个人中心-管理搜索引擎

# TODO

- [x] 主页功能
- [x] 拼音检索
- [x] 书签导出
- [x] 浏览器插件
