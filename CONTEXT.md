# 签签世界

平台无关的在线书签管理器：Web 应用 + 浏览器插件，支持多级目录、拼音检索、浏览器书签导入导出。

## Language

**前端升级**:
指 2026 年启动的前端全面现代化迁移：Vue 2 → Vue 3、vue-cli/webpack → Vite、ant-design-vue 1 → 4、Vuex → Pinia、保持 JavaScript 不转 TS。2026-10 已完成：`bookmark_front` 已切换为 Vue 3 + Vite + antd 4 + Pinia，迁移期间的临时目录已归位。
_Avoid_: 兼容 Node 24（该最低目标现状已满足，不构成需求）

**插件契约页面**:
浏览器插件直接依赖的两个前端页面：`/noHead/addBookmark`(iframe 嵌入，postMessage 通信）和 `/manage/sso/auth`（登录后回传 token)。URL 与消息协议对插件是外部契约，不可变更。
_Avoid_: noHead 页面、sso 页面（单指其一，漏掉另一方）

**Pin**:
固定在 home 页顶部展示的书签（表 `pin_bookmark`)，与目录树中的普通书签是两个概念。
_Avoid_: 置顶、常用书签
