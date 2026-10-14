# AGENTS.md — 前端 bookmark_front

## 技术栈

Vue 2.6（**Options API，不要写 Composition API / `<script setup>`**）+ ant-design-vue 1.7 + Vuex 3 + vue-router 3 + axios + less，脚手架 vue-cli 5，包管理 **pnpm**。

## 命令

```bash
pnpm install   # 装依赖；若因 pnpm 新版本拦截依赖 postinstall 脚本而失败，用 pnpm install --ignore-scripts
pnpm serve     # 本地开发（devServer 已代理 /bookmark/api → 后端 8088）
pnpm build     # 改完必跑，必须全绿
```

> **已知问题**：`pnpm lint` 当前是坏的（`TypeError: ESLint is not a constructor`)——`@vue/cli-plugin-eslint@5` 与锁定的 `eslint@6` 不兼容，属历史遗留。修复前以前端 `pnpm build` + 人工遵守风格约定为准；不要为了让 lint 通过而大规模改写存量代码。

## 目录职责

| 目录 | 职责 |
|---|---|
| `src/views/` | 页面，按路由分目录（`home/`、`manage/`、`noHead/`、`public/`），入口统一 `index.vue`，页面私有组件/helper 平级放同目录 |
| `src/components/` | 可复用组件（`main/`、`public/`） |
| `src/layout/` | 布局组件（`home/`、`manage/`） |
| `src/store/` | `index.js` 组装 + `modules/*.js`（namespaced） |
| `src/router/` | 单文件路由 + `beforeEach` token 校验 |
| `src/util/` | `HttpUtil.js`、`UserUtil.js`、`FileUtil.js` |
| `src/global.less` | 全局 less 变量，在 `App.vue` 非 scoped style 中引入 |

## 组件写法

- 统一 `<template> / <script> / <style lang="less" scoped>` 三段；`export default { name: "PascalCase", ... }`。
- import 用 `@/` 别名；生命周期可以是 `async created() {}` / `async mounted() {}`。
- 尺寸用 rem（`html { font-size: 100px }`，0.16rem = 16px）；局部 less 变量在 `<style>` 顶部声明；全局变量用 `global.less` 里的（`@bgColor`、`@topHeight` 等）。
- ant-design-vue 组件按需 `Vue.use` 集中在 `main.js`；`$message`/`$notification` 挂在 `Vue.prototype`，通过 `window.vueInstance` 访问。

## API 请求 —— 唯一出口 HttpUtil

**禁止组件里直接用 axios。** 一律：

```js
import HttpUtil from "@/util/HttpUtil";
const data = await HttpUtil.get("/searchEngine/list");          // 拿到的就是后端 Result.data
await HttpUtil.post("/user/login", null, this.form);            // post(url, params, body, isForm)
await HttpUtil.put("/user", null, body);
await HttpUtil.post("/common/file", null, formData, true);      // 表单上传 isForm=true
```

`HttpUtil` 内部统一 `baseURL: "/bookmark/api"`、头 `jwt-token`，并按 code 处理（1 返回 data / -1 跳登录 / 0、-2 弹错误提示）。**不要再 `.data`，不要重复处理 code。**

## Vuex 约定

- 模块 namespaced，模块文件同时导出**常量名**与 `store` 对象；action/mutation 用常量做键：

```js
export const GLOBAL_CONFIG = "globalConfig";
const actions = { async [noLoginInit]({ commit }) { ... } };
export const store = { namespaced: true, state, getters, actions, mutations };
```

- 调用方拼 `模块名/常量`；token 持久化用 localforage，由 `setToken` action 写入 `window.jwtToken`。

## 路由约定

`router/index.js` 单文件，`mode: "history"`，**全部懒加载** `component: () => import("@/views/xxx/index")`；按 `home / manage(children) / public(children)` 分组；`"*"` 重定向 `/public/404`。

## 命名与样式

- 目录/view 目录：`lowerCamel`（`bookmarkTree`、`noHead`），入口 `index.vue`；可复用组件 `PascalCase.vue`；工具类 `XxxUtil.js`；store module `lowerCamel.js`。
- 组件 `name` 用 PascalCase；CSS class 用 `lowerCamel`（`.searchContent`）。
- Prettier：`printWidth: 150`、双引号、`bracketSpacing`；ESLint：`plugin:vue/essential`。`vue.config.js` 里 `lintOnSave: false`，所以**必须手动跑 `pnpm lint`**。
