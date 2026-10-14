# AGENTS.md — 后端 bookMarkService

## 技术栈

Spring Boot 3.5.7 / Java 25 / Maven 多模块 / Lombok / MyBatis-Plus / MySQL / Redis / Flyway。**没有 checkstyle/spotless，风格靠本文件约定。**

## 模块结构

```
common            工具类、Result、异常、LoginFilter、Redis MQ 基础设施、全局常量
business/api      跨模块调用的接口（纯接口，无实现）
business/bookmark 书签业务
business/user     用户业务
web               启动入口 Application.java + application*.yml + Flyway 迁移
```

依赖方向：`common ← business.* ← web`。**business 子模块之间禁止直接依赖**，跨模块调用：接口定义在 `business/api`（如 `BookmarkApi`），实现放提供方模块的 `service/impl`（如 `BookmarkApiImpl`，`@Service`）。

包名：`com.fanxb.bookmark.{模块}.{分层}`，分层包固定为 `controller / service / service.impl / dao / entity(po,vo,redis) / constant / consumer / schedule`。

## Controller 约定

- `@RestController` + `@RequestMapping("/xxx")`，**返回值恒为 `Result`**（`common.entity.Result`，无泛型），用 `Result.success(data)` / `Result.success()`。
- HTTP 动词语义：GET 查询、PUT 新增、POST 动作/修改、DELETE 删除。
- 简单标量参数不写注解（`public Result deleteOne(int id)`）；请求体用 `@Valid @RequestBody XxxBody`，校验注解（jakarta.validation）写在 Body 字段上并带中文 message。
- 取当前用户：`UserContextHolder.get().getUserId()`（ThreadLocal，由 `common/filter/LoginFilter` 注入）。

## Service 约定

- **接口 + 实现强制分离**：`service/XxxService` 接口，`service/impl/XxxServiceImpl` 实现，`@Service` 标在 impl 上。
- 依赖注入**优先 Lombok**：`@RequiredArgsConstructor` + `private final`（老代码的显式 `@Autowired` 构造器/字段注入不要新写）。
- 写操作加 `@Transactional(rollbackFor = Exception.class)`；只读方法不加。
- 日志：`@Slf4j` + `log.info("...{}", x)`。

## Entity 约定

| 包 | 内容 | 命名后缀 |
|---|---|---|
| `entity/po/` | 数据库表实体 | `*Po`（`Bookmark`/`User` 等历史类除外） |
| `entity/vo/` | 响应/视图对象，user 模块的请求体也放这 | `*Vo` / `*Res` / `*Body` |
| `entity/`（根） | 请求体、中间传输对象 | `*Body` |
| `entity/redis/` | Redis MQ 消息体 | 消息语义命名 |

PO 写法：`@Data`（按需 `@NoArgsConstructor @AllArgsConstructor @Accessors(chain = true)`）+ `@TableName("表名")`，主键 `@TableId(type = IdType.AUTO)`，非表字段 `@TableField(exist = false)`。

**关键坑**：`map-underscore-to-camel-case: false` —— 数据库列名就是 camelCase，必须与字段名完全一致。

## DAO 约定

- 接口 `dao/XxxDao`，标 `@Component`，`extends BaseMapper<Po>`，参数一律 `@Param("name")`。
- 简单 SQL 用 `@Select`/`@Update` 注解；复杂 SQL 放 `src/main/resources/mapper/`。
- XML 文件名用 **`<模块名>-<dao名>Mapper.xml`**（避免多模块 classpath 同名冲突），`namespace` 写 DAO 全限定名，`resultType` 写全类名。

## 统一返回与异常

- `Result` code 约定：**1 成功 / 0 一般错误 / -1 未认证 / -2 表单错误 / -3 无权限**。Jackson 配置 `non_null`，null 字段不输出。
- 业务错误抛 `CustomException("中文提示")`；表单错误 `FormDataException`；未登录 `NoLoginException`。全局兜底在 `common/exception/ExceptionHandle.java`（`@RestControllerAdvice`）。
- 认证：`LoginFilter` 三段式鉴权——`permissions.yml` 的 public 免登录放行；其余校验请求头 `jwt-token`（失败返回 HTTP 200 + `Result(-1)`）；JWT 通过后经 `UserStateService` 校验 Redis 禁用/密码重置标记（进程内缓存 TTL 60s，不通过按未登录返回 `Result(-1)`）；受权限控制的接口按 `METHOD:路径` 与用户资源 Ant 匹配，不匹配返回 `Result(-3)`，`super_admin` 角色短路放行。

## Redis MQ（自研，不是 RabbitMQ/Kafka）

- 生产者：`RedisUtil.addToMq(topic, obj)`；topic 常量集中定义在 `common/constant/RedisConstant.java`。
- 消费者：`@MqConsumer(topic)` + `implements RedisConsumer`，`deal(String)` 里用 `JsonUtil.string2Obj` 反序列化；注入用 `@RequiredArgsConstructor`。

## 其他约定

- 工具类为静态方法类（`RedisUtil`/`JsonUtil`/`HttpUtil` 等，在 `common/util`）；需要 Spring 资源的用 `@Component` + setter 写 static 字段的既有模式。
- 常量放各模块 `constant/` 或 `common/constant/`；**不用 enum，一律常量类**。
- 定时任务：`@Component` + `@Scheduled`，放模块 `schedule/` 包。
- 类头 javadoc 保留 `类功能简述：/@author fanxb/@date` 风格；新代码至少留 `@author`。
- 格式：4 空格缩进，K&R 花括号。
- DB 变更：在 `web/src/main/resources/db/migration/` 加 `V{n}__中文描述.sql`。

## 单元测试（增量强制）

- 框架：`spring-boot-starter-test`（JUnit 5 + Mockito），父 pom 已统一引入 test scope。
- 位置：与被测类同包，`src/test/java/...`，命名 `XxxServiceImplTest`。
- 形态：**纯单测**，DAO/RedisUtil/其他模块 Api 一律 `mock`，不连真实 MySQL/Redis，不引入 H2/Testcontainers，不起 Spring 容器（不用 `@SpringBootTest`）。
- 验证：`mvn clean install` 全绿。

## 配置

`web/src/main/resources/application.yml`：`server.port=8088`、`context-path=/bookmark/api`。部署期配置经 docker-compose 的 `-D` 参数注入，新增配置项需同步更新 compose。

`web/src/main/resources/permissions.yml`：权限/资源/公共 URL 的唯一定义来源，启动时 fail-fast 校验；新增受控接口需在此登记（漏登记 = 非超管 403）。
