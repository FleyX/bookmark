本项目是一个云书签的项目，取名为：签签世界。

demo 地址:[ali.tapme.top:8083](http://ali.tapme.top:8083)

# 缘由

1. 主要用的是 chrome，但是有时候需要用其他的浏览器：Firefox，ie 等。然后这些浏览器上没有书签，想进个网站还得打开 chrome 复制 url，太麻烦。

2. chrome 必须翻墙才能同步书签，体验不是那么好。

3. 如果书签全放在 chrome 上，相当于绑定死 chrome 浏览器了，很难迁移到别的优秀浏览器，比如 firfox 上。

所以有了这样这样一个项目，建立一个和平台无关的书签管理器，可在任意平台使用。

计划开发顺序如下：web 端->chrome 插件->firfox 插件->web 端兼容手机等小屏幕。

最终目的就是所有浏览器(不包含 ie10 及以下等远古浏览器)中都能便捷的使用书签。

# 开发进度

基本可用了。

## 2019-06-27

**tag: 第一篇：环境搭建**

前端 react 框架搭建完成。

## 2019-07-10

**tag: 第二篇：注册登录重置密码完成**

后台框架搭建，并完成以下功能：

- 登录，注册，重置密码，发送验证码接口完成
- 书签 html 上传解析并存到数据库,仅测试了chrome导出的书签文件
- 查询某个用户的书签树

前台完成以下功能：

- 注册，登录，重置密码界面完成

## 2019-07-22

增删改查功能完成。支持节点拖拽

## 2019-07-30

- docker部署重新整理,部署更方便了。
- 加入elasticsearch全文检索，可以方便的搜索书签啦。
- 树节点增加右键菜单，更加便捷的增删改