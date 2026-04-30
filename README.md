# softwarestore-server

基于关联规则挖掘（Apriori 算法）的软件下载平台后端服务。

## 技术栈

- **JDK 21**
- **Spring Boot 3.5.14**（Spring Framework 6.2）
- **Spring Security** — 无状态 JWT 认证
- **Spring Data JPA**（Hibernate）— ORM 数据访问
- **MySQL** — 关系型数据库
- **springdoc-openapi** — OpenAPI 3.0 文档（Swagger UI）
- **Maven** — 构建工具

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.6+（或使用项目自带的 `mvnw`）

### 数据库配置

创建 MySQL 数据库：

```sql
CREATE DATABASE software_store DEFAULT CHARACTER SET utf8mb4;
```

数据库连接信息分别在 `application-dev.properties` 和 `application-prd.properties` 中配置。

### 构建与运行

```bash
# 开发环境运行（端口 8080）
./mvnw spring-boot:run

# 生产环境运行
./mvnw spring-boot:run -Dspring-boot.run.profiles=prd

# 构建 JAR 包
./mvnw package

# 运行测试
./mvnw test
```

启动后访问：
- API 文档：http://localhost:8080/swagger-ui/index.html
- Actuator：http://localhost:8080/actuator

## 前端

Web 前端项目：https://github.com/ValueXu/software-store-front

## 项目结构

```
src/main/java/cn/jxufe/valuexu/softwarestoreserver/
├── config/          # 安全配置、CORS、Swagger、常量
├── controller/      # REST 控制器（业务逻辑集中在此）
├── dao/             # Spring Data JPA Repository
├── domain/          # JPA Entity 实体类
├── dto/             # 数据传输对象（统一响应包装器）
├── exception/       # 自定义异常基类
├── filter/          # JWT 认证过滤器
├── scheduler/       # 定时任务（关联规则挖掘）
├── service/         # 业务服务（关联规则挖掘 Apriori 算法）
└── utils/           # 工具类（JWT 工具）
```

## 关联规则挖掘

系统每 5 分钟执行一次关联规则挖掘：加载所有用户的下载记录作为事务数据，使用 Apriori 算法挖掘软件间的关联关系，结果写入 `association_rule` 表，用于推荐相关软件。

参数：最小支持度 0.05，最小置信度 0.6。
