# AGENTS.md — 签签世界（在线书签管理）

本文件是 agent 开发本仓库的总入口。**在子目录内工作时，优先遵守该目录下更近的 AGENTS.md。**

## 项目简介

平台无关的在线书签管理器，线上地址 https://bm.fleyx.com。功能：浏览器书签导入/导出、树型多级目录、拼音检索、浏览器插件右键添加书签。

## 仓库结构

| 目录 | 说明 | 详细规则 |
|---|---|---|
| `bookMarkService/` | 后端，Maven 多模块（Spring Boot 3.5 / Java 25） | `bookMarkService/AGENTS.md` |
| `bookmark_front/` | 前端，Vue 3 + ant-design-vue 4 + Vite，pnpm | `bookmark_front/AGENTS.md` |
| `浏览器插件/bookmarkBrowserPlugin/` | Chrome 插件（MV3，原生 JS，无构建） | `浏览器插件/AGENTS.md` |
| `data/` | 部署用数据/配置（mysql、nginx、maven 仓库缓存等），不要改业务代码时动它 | — |

## 构建与验证命令（本地）

本机已具备 JDK 25、Maven 3.9、Node + pnpm，验证一律用本地命令：

```bash
# 后端：编译 + 单元测试（改后端代码后必须全绿）
cd bookMarkService && mvn clean install

# 前端：构建 + lint（改前端代码后必须全绿）
cd bookmark_front && pnpm build
cd bookmark_front && pnpm lint
```

根目录 `build.sh` 是**服务器部署脚本**（docker 内构建 + 插件打 zip），本地开发不要跑它；CI（`.drone.yml`）在 master 推送后到服务器执行它。

## 部署

`docker-compose.yml` 服务：mysql、redis、front(nginx)、iconserver、backend。敏感配置走根目录 `.env`（**严禁读取、打印或提交其内容**）。后端运行时环境变量在 compose 的 `command` 中逐个 `-D` 传入，新增配置项需同步加在这里。

## 分支与提交

- 分支模型：`dev` 开发 → PR 合入 `master` → drone 自动部署。agent 的工作分支从 `dev` 切出。
- commit message：中文，前缀类型如 `fix: ` / `deploy: ` / 功能描述，参照 `git log` 现有风格。

## Agent 工作流约束（必须遵守）

1. **代码风格与存量保持一致**：先读相邻代码再动手；本仓无 Java 格式化工具，风格规则靠各子模块 AGENTS.md 明文约定。
2. **改完必须验证**：后端 `mvn clean install`（含单测）全绿；前端 `pnpm build` 与 `pnpm lint` 全绿。没有验证通过不算完成。
3. **后端增量单测强制**：新增或修改的 service/业务逻辑必须配单元测试（JUnit 5 + Mockito，外部依赖全部 mock，不连真实 MySQL/Redis）。存量代码暂不强求补测。
4. **数据库变更必须加 Flyway 迁移**：`web/src/main/resources/db/migration/V{n}__中文描述.sql`，编号顺延，不改已发布的迁移文件。
5. **跨模块调用必须走 `business/api`**：禁止 business 子模块之间直接依赖实现类。
6. **不引入新依赖前先确认**：优先使用已有依赖；确需新增时在变更说明中列出理由。
7. **插件无构建工具**：改完不需要编译，部署时 `build.sh` 会自动重新打 zip。

## 已知现状（历史遗留，除非任务要求否则不要动）

- ES 检索与独立拼音服务已废弃：compose 中已移除 `es.host`、`pinyin.*` 配置；拼音现用本地库 `houbb pinyin`。代码中残留的 `BookmarkEs`、`EsConstant`、`EsException` 等类暂保留。
- 后端运行时镜像为 `eclipse-temurin:25-jre`（与代码 Java 25 匹配）。
- 插件 `background.js` 中 `if (!data.code || !data.receiver == 'background')` 是存量 bug（运算符优先级），如非相关任务不要顺手改。
