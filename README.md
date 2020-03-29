本项目是一个云书签的项目，取名为：签签世界。

访问地址:[bm.tapme.top](http://bm.tapme.top)

web端已经完成。

# 缘由

1. 主要用的是 chrome，但是有时候需要用其他的浏览器：Firefox，ie 等。然后这些浏览器上没有书签，想进个网站还得打开 chrome 复制 url，太麻烦。

2. chrome 必须翻墙才能同步书签，体验不是那么好。

3. 如果书签全放在 chrome 上，相当于绑定死 chrome 浏览器了，很难迁移到别的优秀浏览器，比如 firfox 上。

所以有了这样这样一个项目，建立一个和平台无关的书签管理器，可在任意平台使用。

计划开发顺序如下：web 端->chrome 插件->firfox 插件。

最终目的就是所有浏览器(不包含 ie10 及以下等远古浏览器)中都能便捷的使用书签。

# 主要功能

## 查

1. 节点树展示书签
   ![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801185846.png)
   采用懒加载方式加载每一层数据，即使大量数据也不会卡顿。

2. 全文检索<br>
    支持拼音、拼音首字母、关键词查找

- 可对`书签名`和`链接`进行全文检索
- 支持方向键-上/下切换，回车确认
- 可直接搜索 google/baidu,tab 键切换。
  ![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801190720.png)
- 支持右键复制 url(移动端不支持右键，需点击编辑-->菜单键)
  ![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801191010.png)

## 增

![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801191452.png)

1. 手动编辑导入
   ![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801191601.png)
2. 谷歌、火狐浏览器书签备份文件直接导入，相同名称的将都保留
   ![](https://raw.githubusercontent.com/FleyX/files/master/blogImg/20190801191721.png)

## 改

1. 修改节点内容，右键->编辑。（移动端长按相当于右键）
2. 修改书签顺序，所属文件夹，直接拖拽书签到目标位置

## TODO

- 浏览器插件
- 拼音检索 Ok!
- 侧边栏显示
