# AGENTS.md — 浏览器插件 bookmarkBrowserPlugin

Chrome 扩展（Manifest V3），**原生 JS，无 npm、无构建工具、无模块化**。改动后无需编译；部署时根目录 `build.sh` 会把本目录整体打成 zip 放到 `bookmark_front/public/static/bookmarkBrowserPlugin.zip`。

## 结构

| 文件 | 职责 |
|---|---|
| `manifest.json` | MV3 清单，permissions：`contextMenus`、`storage` |
| `background.js` | service worker：右键菜单、token 存取（`chrome.storage.local`） |
| `popup/` | 点击图标的弹窗页 |
| `options/` | 设置页（当前为空壳占位） |
| `tab/` | 新标签页，meta refresh 直接跳转 https://bm.fleyx.com |
| `static/js/` | `config.js`（全局配置）、`content.js`（content script）、第三方库（axios.min.js、jquery.js） |

## 风格约定（与存量保持一致，不要"顺手现代化"）

- `var` + `function` 声明与 `async/await`、箭头函数、模板字符串混用是现状，**保持所在文件既有风格**，不全局改写。
- 无 import/export：HTML 中用多个 `<script>` 按顺序加载全局脚本（先 `axios.min.js` 再 `config.js`）。
- 缩进 2 空格，K&R 花括号。
- 不接入前端那套 ESLint/Prettier。

## 消息通信约定

三部分（background / content / popup）之间统一信封：

```js
{ code: "事件名", data: 任意, receiver: "background" | "content" | "popup" | "page" }
```

- 通道：`chrome.runtime.sendMessage` / `chrome.tabs.sendMessage` / `window.postMessage`（页面 ↔ content），无长连接。
- 已有事件：`addBookmark`、`setToken` / `setTokenOk` / `getToken` / `clearToken`、`getBookmarkData` / `addBookmarkAction` / `closeIframe`。新增事件沿用同一信封。

## 与后端的交互

- 全局配置在 `static/js/config.js`：`bookmarkHost`、`version`、axios 默认值（`baseURL = bookmarkHost + '/bookmark/api'`，头 `jwt-token`）。
- token 存 `chrome.storage.local`（popup 里另读 `localStorage.token`）；JWT 有效性判断 `checkTokenValid` 目前各文件重复实现——**新代码复用所在文件的既有实现，不要再复制第三份**。
- 登录流程：跳后台 `/manage/sso/auth`，token 经 `window.postMessage` → content → background 存储。
- 新增书签：content 以 iframe 嵌入 `bookmarkHost + "/noHead/addBookmark?token=" + token`，网页用 `postMessage({code:'addBookmarkAction'})` 回传。

## 注意

- `bookmarkHost` 硬编码为 `https://bm.fleyx.com`，本地调试需手动改，**不要提交改后的值**。
- 修改 `manifest.json` 的 permissions/结构会影响已安装用户，变更需在提交说明中写明。
