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

**超级管理员**:
内置角色（角色 code 判定，非用户表标志位），拥有全部权限：后端鉴权时若用户拥有该角色直接放行，前端视为拥有全部页面资源。该角色不可编辑/删除/禁用。产生方式：系统首个注册用户自动绑定该角色（须在 README 中说明）。
_Avoid_: 超管（指旧 `manageUserId` 配置的单管理员机制，已被角色体系取代）、is_admin

**页面级权限 / URL 级权限**:
权限控制的两个粒度。前端只做到页面级：路由按资源 code 过滤，无权限页面不可见、不可进。后端做到 URL 级：每个请求按 `METHOD:路径` 与用户角色拥有的资源比对，不含则拒绝。

**角色（role）**:
权限的载体，落库。用户-角色多对多，权限取并集。角色可禁用（禁用后已绑用户立即失去对应权限）；有用户绑定时禁止删除；super_admin 不可改/删/禁用。
_Avoid_: 用户组、岗位

**权限（permission）**:
资源的打包分组，**不落库**，唯一定义来源是 `permissions.yml`（启动 fail-fast 校验）。角色绑定权限 code。当前权限树：`system:user`（用户管理）、`system:role`（角色管理）、`ops:bookmark`（书签运维三个批处理）。
_Avoid_: 直接把权限当资源用

**资源（resource）**:
最小鉴权单元，**不落库**，随权限定义在 `permissions.yml`。code 格式：`METHOD:路径`（后端接口，Ant 风格匹配）或 `VIEW:路径`（前端页面）。登录接口 `/user/currentUserInfo` 返回当前用户已展开的资源列表（`resources` 字段），供前端过滤路由与菜单。

**公共 URL**:
免登录接口，定义在 `permissions.yml` 的 public 段（原 url 表 type=0 机制已废弃）。其余接口默认"登录即可"，仅权限树覆盖的接口需要角色资源。

**账号禁用**:
管理员可将用户置为禁用。禁用标记写 Redis，最长 60 秒全网生效；被禁用户的请求按未登录处理（code -1 跳登录页），登录时提示"账号已被禁用"。管理员重置密码同理使旧 JWT 失效。

**权限缓存**:
用户权限解析结果进程内缓存，角色/绑定变更时主动失效——管理操作即时生效（单实例部署前提）。与账号禁用的 Redis 标记是两个独立机制。

**管理员**:
拥有管理类权限角色的用户的统称（如 super_admin，或绑定了 `system:user`/`system:role`/`ops:bookmark` 权限的角色）。旧的 `manageUserId` 配置机制已废弃删除。
_Avoid_: 角色、权限组、超管（特指 super_admin 角色）

**管理后台**:
`/admin` 下按页面级权限展示的管理界面，目前包含用户管理和角色管理；不同于普通用户的 `/manage` 书签管理界面。
_Avoid_: 管理页（指单个页面）、书签管理
