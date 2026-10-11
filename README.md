# Data Platform Lite

一个面向中小团队的数据接入、同步、质量检查和指标服务平台示例。

> 这是一个以求职面试为目标设计的企业级后端/数据开发项目，重点展示任务调度、数据同步、质量规则、指标服务和工程化实践，而不是简单 CRUD。

## 项目目标

```text
数据源 -> 同步任务 -> ODS/DWD/DWS/ADS -> 质量检查 -> 指标 API
```

当前版本已经包含一个可演示的企业级后端核心：

- 数据源管理：MySQL / CSV / OSS 等数据源配置
- 同步任务管理：全量/增量任务元数据、状态、调度表达式
- 任务执行中心：执行历史、运行状态、处理行数和失败信息
- 幂等执行：支持 `Idempotency-Key`，避免重复触发重复运行
- 数据质量规则：规则 CRUD，为 NOT_NULL / UNIQUE / NON_NEGATIVE 等规则扩展预留结构
- 指标服务：流水线成功率、处理行数、运行次数
- 审计事件：记录任务运行成功/失败等关键操作
- PostgreSQL 生产环境 + H2 测试环境
- Docker Compose 一键启动
- Caffeine 本地指标缓存
- GitHub Actions 自动测试和打包

## 技术栈

- Java 21
- Spring Boot 3.4
- Spring JDBC
- PostgreSQL / H2
- Docker Compose
- GitHub Actions

## 快速启动

### 本地测试

需要 Maven 3.9+ 和 JDK 21+：

```bash
mvn test
mvn spring-boot:run
```

### Docker 启动

```bash
docker compose up --build
```

服务启动后访问：

- API：`http://localhost:8080`
- 健康检查：`http://localhost:8080/actuator/health`

## 系统架构

```text
                    +----------------------+
                    |  REST API / Console  |
                    +----------+-----------+
                               |
             +-----------------+------------------+
             |                 |                  |
       Data Source       Sync Task          Quality Rules
             |                 |                  |
             +-----------------+------------------+
                               |
                    +----------v-----------+
                    |   Task Run Engine    |
                    | idempotency / audit  |
                    +----------+-----------+
                               |
                    +----------v-----------+
                    | PostgreSQL / H2      |
                    +----------------------+
```

## API 示例

创建数据源：

```bash
curl -X POST http://localhost:8080/api/v1/data-sources \
  -H "Content-Type: application/json" \
  -d '{"name":"orders-db","type":"MYSQL","connectionConfig":"jdbc:mysql://localhost/orders"}'
```

创建同步任务：

```bash
curl -X POST http://localhost:8080/api/v1/sync-tasks \
  -H "Content-Type: application/json" \
  -d '{"name":"sync-orders","sourceId":1,"targetTable":"ods_order","syncMode":"FULL","scheduleExpression":"0 0 1 * * ?"}'
```

触发同步任务（幂等）：

```bash
curl -X POST http://localhost:8080/api/v1/sync-tasks/1/runs \
  -H "Idempotency-Key: demo-run-20261011"
```

查看任务执行历史：

```bash
curl http://localhost:8080/api/v1/sync-tasks/1/runs
```

创建数据质量规则：

```bash
curl -X POST http://localhost:8080/api/v1/quality/rules \
  -H "Content-Type: application/json" \
  -d '{"name":"order amount non-negative","tableName":"ods_order","ruleType":"NON_NEGATIVE","expression":"amount","severity":"ERROR"}'
```

查看平台指标：

```bash
curl http://localhost:8080/api/v1/metrics/overview
```

## 迭代路线

- [x] 项目骨架、数据库初始化和 REST API
- [x] 数据源与同步任务元数据管理
- [x] 任务执行记录、幂等键和审计事件
- [x] 指标 API、Caffeine 缓存和质量规则 API
- [x] Docker Compose 和 CI
- [ ] Quartz/XXL-JOB 调度执行器
- [ ] CSV/OSS/MySQL 实际同步适配器
- [ ] 幂等写入、断点续传和失败重试
- [ ] 数据质量规则执行引擎
- [ ] ODS-DWD-DWS-ADS 示例数仓
- [ ] Kafka + Flink 实时指标链路
- [ ] ClickHouse/Doris 指标查询
- [ ] Vue 管理控制台

## 面试可讨论的设计点

1. 全量同步和增量同步如何选择？
2. 任务重复执行时如何保证幂等？
3. 批量写入失败后如何断点恢复？
4. 数据质量规则如何配置和扩展？
5. 指标 API 如何做缓存和权限隔离？
6. Kafka 重复消费、消息积压和死信如何处理？

## License

MIT
