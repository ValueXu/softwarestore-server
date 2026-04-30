# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 构建与运行

```bash
# 构建并运行（dev 配置，端口 8080）
mvn spring-boot:run

# 使用生产环境配置运行
mvn spring-boot:run -Dspring-boot.run.profiles=prd

# 构建 JAR
mvn package

# 运行所有测试（目前只有一个 context-load 测试）
mvn test

# 运行单个测试类
mvn test -Dtest=SoftwarestoreServerApplicationTests
```

需要系统安装 Maven（`mvn`）。

## 架构概览

Spring Boot 3.5.14（JDK 25）REST API，实现基于关联规则挖掘的软件下载平台。MySQL 数据库通过 Spring Data JPA（Hibernate）访问。API 文档位于 `/swagger-ui/index.html`（springdoc-openapi v2.8.17）。

### 分层约定

- **`controller/`** — REST 控制器。**业务逻辑集中在此**，而非 service 层。控制器通过 `@Resource` 直接注入 Repository。
- **`service/`** — 大部分是空壳。`UserService`、`SoftwareService`、`RecordService`、`ScoresService` 只有类定义没有逻辑。唯一有实际代码的是 `service/associationrulemining/AssRuleMiningService`。
- **`dao/`** — Spring Data JPA Repository，继承 `JpaRepository`。
- **`domain/`** — JPA `@Entity` 类，映射到 MySQL 表。
- **`dto/`** — `WrappedResDto<T>` 是统一的 API 响应包装器（code/msg/result）。`dto/response/` 中有类型化的响应 DTO，但控制器大多数直接返回原始 `HashMap` 或 `List`，类型化 DTO 基本未使用。
- **`config/`** — 安全配置、CORS、Swagger、常量、`ResCodeEnum`（全局响应码）。
- **`filter/`** — `JWTAuthenticationFilter` — 无状态 JWT 认证过滤器，放在 `UsernamePasswordAuthenticationFilter` 之前。
- **`scheduler/`** — `AssociationScheduler` 每 5 分钟执行一次 Apriori 关联规则挖掘。
- **`exception/`** — 抽象基类 `AbstractBizException` 和 `AbstractSysException` 已定义，但项目中暂无使用。

### 认证流程

无状态 JWT。登录成功后，服务端签发 `Bearer <token>`，token 中包含 `username` 和 `type` 声明。`JWTAuthenticationFilter` 对每个请求验证 token，从数据库查找用户，并设置 `SecurityContextHolder`。安全白名单（无需 token）覆盖：`/user/signIn*`、`/user/signUp*`、`/software*`、`/software/getAllByType*`、`/login*`、`/error`、`/actuator`。

**重要：** JWT 签名密钥在每次启动时随机生成（`Jwts.SIG.HS256.key().build()`），重启后所有 token 失效。

**注意：** `JWTAuthenticationFilter.matchWhitListPatterns()` 使用 `String.matches()` 进行白名单匹配，因此 `/software*` 会匹配所有以 `/software` 开头的任意路径（而非字面量星号）。

### CORS

`CorsConfig` 允许所有来源（`*`）、所有请求头、所有 HTTP 方法，并启用凭据——实际上完全开放。

### 关联规则挖掘

`AssociationScheduler` 触发 `AssRuleMiningService.getRecordsForRulesMining()`，该方法加载所有用户的下载记录作为事务数据，传入 `AssociationRuleMinner`（Apriori 算法），结果写入 `association_rule` 表。参数：minSupport=0.05，minConfidence=0.6，每 5 分钟执行。

**已知代码问题：** `AssociationRuleMinner.isAssociationRules()` 第 273 行调用了 `associationRuleRepository.saveAndFlush()`，但该类并未声明或注入该字段——代码可能无法编译或依赖外部注入。

### 数据模型关系

- **User**（id, username, password, type, email）— type 0=管理员，3/4=普通用户
- **Software**（id, name, author, type, download_url, score）— `author` 为用户名字符串，非外键
- **Record**（software_id, username, time）— 下载记录，用作关联规则挖掘的事务数据
- **Scores**（username, software_id, score, comment）— 用户评分；每次添加/删除评分后重新计算 Software 平均分
- **AssociationRule** — 每条规则最多存储 5 个软件 ID 及支持度和置信度
- **Rules** — 似乎是未使用的残留实体

### 数据库

MySQL，地址 `127.0.0.1:3306/software_store`。JPA `hbm2ddl.auto=update` 自动创建/更新表结构。开发环境账号：`app_software_store/123456`。生产环境：`root/123456`。

### 开发环境

JDK 25 + Spring Boot 3.5.14。使用 Maven Wrapper（`mvnw`）构建，无需系统安装 Maven。开发数据库 `app_software_store/123456`，生产数据库 `root/123456`。

### springdoc-openapi 版本说明

springdoc-openapi 版本号与 Spring Boot 版本对应：v2.x（starter-webmvc-ui）对应 Spring Boot 3.x，v1.x（openapi-ui）对应 Spring Boot 2.x，v3.x 对应 Spring Boot 4.x。当前项目使用 v2.8.17，Swagger UI 路径为 `/swagger-ui/index.html`。
