# AGENTS.md — 前端 bookmark_front

## 技术栈

Vue 3.5（**Options API，不要写 Composition API / `<script setup>`**）+ ant-design-vue 4 + Pinia 3 + vue-router 4 + axios + localforage + less + dayjs，构建工具 **Vite 7**，包管理 **pnpm**。

## 命令

```bash
pnpm install   # 装依赖；若因 pnpm 新版本拦截依赖 postinstall 脚本而失败，用 pnpm install --ignore-scripts（pnpm-workspace.yaml 的 allowBuilds 已放行 esbuild、禁用 core-js）
pnpm dev       # 本地开发（Vite dev server 端口 4531，已代理 /bookmark/api → 后端 8088）
pnpm build     # 改完必跑，必须全绿，产物在 dist/
pnpm lint      # eslint 9（flat config）+ prettier --check，改完必跑，必须全绿
```

Vite 不做 `lintOnSave`，所以**必须手动跑 `pnpm lint`**，不要为了让 lint 通过而大规模改写存量代码。

## 目录职责

| 目录 | 职责 |
|---|---|
| `src/views/` | 页面，按路由分目录（`home/`、`manage/`、`admin/`、`noHead/`、`public/`），入口统一 `index.vue`，页面私有组件/helper 平级放同目录 |
| `src/components/` | 可复用组件（`main/`、`public/`） |
| `src/layout/` | 布局组件（`home/`、`manage/`、`admin/`） |
| `src/store/` | `index.js` 创建 pinia 实例并编排登录/登出初始化 + `globalConfig.js`、`treeData.js`、`user.js`（`defineStore`，`user.js` 存当前用户信息与已展开资源，供页面级权限判定） |
| `src/router/` | 单文件路由 + `beforeEach` token 校验与 `/admin` 页面级权限拦截 |
| `src/util/` | `HttpUtil.js`、`UserUtil.js`、`FileUtil.js` |
| `src/assets/global.less` | 全局 less 变量，`main.js` 引入；`App.vue` 用 `@import (reference)` 引变量 |

## 组件写法

- 统一 `<template> / <script> / <style lang="less" scoped>` 三段；`export default { name: "PascalCase", ... }`。
- import 用 `@/` 别名（`vite.config.js` 中 alias 到 `src/`）；生命周期可以是 `async created() {}` / `async mounted() {}`。
- 尺寸用 rem（`html { font-size: 100px }`，0.16rem = 16px）；局部 less 变量在 `<style>` 顶部声明；全局变量用 `global.less` 里的（`@bgColor`、`@topHeight` 等）。
- ant-design-vue 4 在 `main.js` 中整体 `app.use(Antd)`；iconfont 图标用全局组件 `<my-icon type="..." />`。
- `$message`/`$notification`/`$confirm` 挂在 `app.config.globalProperties`（组件内 `this.$message`）；`HttpUtil`、store 这类非组件模块直接 `import { message, notification } from "ant-design-vue"`。
- `main.js` 挂载后把 app 实例赋给 `window.vueInstance`，并给 `globalProperties.$store` 挂了只读 shim（`pinia.state.value`），兼容旧工具类里 `window.vueInstance.$store.state.模块名` 的写法。

## API 请求 —— 唯一出口 HttpUtil

**禁止组件里直接用 axios。** 一律：

```js
import HttpUtil from "@/util/HttpUtil";
const data = await HttpUtil.get("/searchEngine/list");          // 拿到的就是后端 Result.data
await HttpUtil.post("/user/login", null, this.form);            // post(url, params, body, isForm)
await HttpUtil.put("/user", null, body);
await HttpUtil.post("/common/file", null, formData, true);      // 表单上传 isForm=true
```

`HttpUtil` 内部统一 `baseURL: "/bookmark/api"`、头 `jwt-token`，并按 code 处理（1 返回 data / -1 跳登录 / 0、-2、-3 弹错误提示）。**不要再 `.data`，不要重复处理 code。**

## Pinia 约定

- 模块用 `defineStore("模块名", { state, getters, actions })`；组件里取状态用 `...mapState(useXxxStore, ["a", "b"])`。
- 组件外的代码（路由守卫、`HttpUtil`、初始化编排）取 store 必须显式传 pinia 实例：`useGlobalConfigStore(pinia)`。
- token 持久化用 localforage，由 `setToken` action 写入 `window.jwtToken`。
- **写 localforage 前必须深拷贝**（`JSON.parse(JSON.stringify(value))`）：Pinia 的响应式代理不能被 IndexedDB 结构化克隆，参考 `store/treeData.js` 的 `toStorable`。
- 各模块的初始化编排集中在 `store/index.js`：`noLoginInit`（未登录初始化）、`loginInit`（登录后初始化）、`logoutClear`（登出清理）。

## 路由约定

`router/index.js` 单文件，`createWebHistory()`，**全部懒加载** `component: () => import("@/views/xxx/index.vue")`；按 `home / manage(children) / public(children)` 分组；`/:pathMatch(.*)*` 重定向 `/public/404`。

**路径清单是外部契约，不可随意变更**：`/noHead/addBookmark`（插件 iframe 嵌入，postMessage 通信）与 `/manage/sso/auth`（插件登录后回传 token）被浏览器插件直接依赖，其余路径变更也要同步检查插件与后端。

## 命名与样式

- 目录/view 目录：`lowerCamel`（`bookmarkTree`、`noHead`），入口 `index.vue`；可复用组件 `PascalCase.vue`；工具类 `XxxUtil.js`；store 模块 `lowerCamel.js`。
- 组件 `name` 用 PascalCase；CSS class 用 `lowerCamel`（`.searchContent`）。
- Prettier：`printWidth: 150`、双引号、`bracketSpacing`、`bracketSameLine`、`htmlWhitespaceSensitivity: "ignore"`；ESLint 9 flat config（`eslint.config.js`：`js.recommended` + `pluginVue.configs["flat/essential"]` + `eslint-config-prettier`，`src/views/**` 关闭 `vue/multi-word-component-names`）。

## 环境变量

GitHub OAuth 登录的 client id 走 `VITE_GITHUB_CLIENT_ID`，在 `.env.development` / `.env.production` 中配置，代码里用 `import.meta.env.VITE_GITHUB_CLIENT_ID` 读取。
