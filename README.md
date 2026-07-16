# SyncTask Engine — 轻量级业务任务编排引擎

## 背景

### 起源：订单中心的统一同步任务调度

2024年，订单中心（cloud-order）需要将销售/采购/售后/开票等单据同步到 10+ 个外部系统（聚水潭、无仓、金蝶、FMS、抖音、快手、金碟等）。最初每接入一个新系统就写一个独立的 xxl-job Handler，很快暴露了几个问题：

- **重复代码太多**：每个 Job 都要自己写失败重试、并发控制、错误告警
- **扩展性差**：每次加新系统都要改调度配置，容易出错
- **缺乏可观测性**：任务失败全靠人工看日志

### 演进：从定制化到通用化

**第一阶段（2024.08 — cloud-order）**：设计 SyncTaskJob 统一编排入口 + SyncTaskHandlerManager 自动注册框架。采用策略模式实现"新增系统只需实现一个 Handler 类"的开闭原则。线上运行 49 个 Handler，覆盖 10 个外部系统。

**第二阶段（2025.08 — cloud-wms）**：WMS 中心（cloud-wms）参照 cloud-order 的实现独立开发了自己的 SyncTaskJob 框架，44 个 Handler。两个项目各自维护相同的编排逻辑、注册表、日志工具和拒绝策略。

**第三阶段（2026.07 — 本引擎）**：识别到两个项目存在 80% 以上的代码重复后，将共性逻辑抽离为本独立引擎组件，同时基于实际经验进行了架构升级：引入 SPI 扩展点、解除 xxl-job 耦合、支持多种通知渠道。

---

## 与业界方案的对照

在设计本引擎之前，调研了以下开源方案：

| 方案 | 类型 | 为什么没直接采用 |
|------|------|-----------------|
| **xxl-job** | 任务调度框架 | 上下层关系（xxl-job 是闹钟，引擎是起床后的流程）。xxl-job 只负责"什么时候触发"和"路由到哪个节点"，不负责"触发之后怎么组织任务执行" |
| **Temporal** | Durable Workflow Engine | 需要独立 Server + 运维 Cluster，且自带调度体系，与公司已有的 xxl-job 基础设施冲突 |
| **Conductor** | DAG 工作流编排 | 同上，独立 Server + ElasticSearch，运维成本高 |
| **nFlow** | 嵌入式 JVM 工作流引擎 | 设计理念最接近（DB 状态机 + 嵌入式），但引入了完整的 FSM 概念，学习成本高 |
| **Flowable** | BPMN 审批引擎 | 领域不同——解决人工审批流程（BPMN），本引擎解决系统间自动同步 |
| **DBOS Transact** | 持久化执行库 | 要求 Postgres，公司只有 MySQL |

### 为什么选择自研

五个约束条件决定了"寄居在 xxl-job 上自研编排层"是当时的最优解：

1. **公司所有定时任务基于 xxl-job** — 不能引入第二套调度体系
2. **团队规模小**（传统行业 IT） — 无法维护独立部署的引擎集群
3. **MySQL 是唯一的基础设施** — 所有开源方案要么要 Postgres、要么要独立 Server
4. **外部系统 10+ 且持续增加** — 必须有可扩展架构
5. **外部系统不可靠** — 必须有持久化的任务状态机

> 详细的选型分析和设计决策，请参见 [SyncTaskJob 技术全景分析](https://github.com/xuh/cloud-order/tree/master/docs)

---

## 核心设计思想

### 1. 引擎与调度分离

```
┌──────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  xxl-job      │    │  SyncTaskEngine   │    │  NotifyChannel  │
│  (闹钟)       │───→│  (编排引擎)       │───→│  (通知渠道)      │
│               │    │                   │    │                 │
│ cron 触发     │    │ 解析→校验→路由→    │    │ 企微/飞书/钉钉  │
│ 路由/分片     │    │ 拉取→过滤→并发→    │    │ /邮件/短信      │
│               │    │ 状态管理→告警      │    │                 │
└──────────────┘    └──────────────────┘    └─────────────────┘
        ↑                      ↑                      ↑
   SchedulerAdapter      HandlerRegistry        NotifyChannel(SPI)
   (SPI: 可换调度框架)    (自动发现注册)          (SPI: 可换通知渠道)
```

### 2. 架构减法哲学

引擎刻意不做的事情，和它做的事情同样重要：

| 做了 | 没做 | 原因 |
|------|------|------|
| Handler 自动注册发现 | DAG 编排引擎 | 90% 的同步任务间无依赖关系；有依赖时用 afterExecute 埋点 + Choreography 模式覆盖 |
| 状态加权拉取调度 | 独立任务调度 Server | 寄居在 xxl-job 上，复用 cron 能力 |
| 多 Job 分工 + 差异化 cron = 每种状态独立的重试心跳 | 引擎内部延迟队列/退避算法 | 用组合现有基础设施替代写代码 |
| 三级告警阈值策略 | 完整的消息队列 | MySQL sync_task 表就是任务队列，够用且可控 |

### 3. 七个 SPI 扩展点

| SPI | 解决的原耦合 | 接入方实现 |
|-----|------------|-----------|
| **SchedulerAdapter** | 解除对 xxl-job 的耦合（XxlJobHelper.getJobParam() / XxlJobHelper.log()） | 实现 xxl-job / PowerJob / 手动触发适配器 |
| **SyncTaskParamParser** | 解除参数解析对 Jackson/JSON 特定实现的耦合 | 实现自定义解析逻辑，或直接配置 param-class 使用默认实现 |
| **TaskStore** | 解除对 ISyncTaskService（MyBatis-Plus）的耦合 | 实现 MyBatis / JPA / Mongo / 外部 API |
| **NotifyChannel** | 解除对 QWRobotUtil（企微机器人）的耦合 | 实现企微 / 飞书 / 钉钉 / 邮件 |
| **LockService** | 解除对 @XLock 注解框架的耦合 | 实现 Redisson / @XLock 适配 / DB 行锁 |
| **SyncTaskParamValidator** | 解除对业务枚举（OrderTypeEnum/SystemEnum）的耦合 | 各项目实现自己的校验逻辑 |
| **TaskLifecycleListener** | 补充缺失的生命周期事件（原设计没有） | Metrics 埋点 / 审计日志 / 编排链 |
| **TaskArchiveService** | 补充历史任务归档能力（原设计没有） | 实现历史表迁移/物理删除，用 xxl-job cron 触发 |

### 4. 状态机设计

```
                      ┌─────────────────────────┐
                      │      sync_task 表        │
                      │  (既是任务队列也是状态机)   │
                      └─────────────────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              ▼                  ▼                  ▼
           INIT                FAIL               WAIT
        (新任务, 2min)      (失败, 10min)       (等待, 5min)
              │                  │                  │
              └──────────────────┼──────────────────┘
                                 │
                          refreshSyncTask
                         (@Lock 防重 + 重新查库)
                                 │
                                 ▼
                           PROCESSING
                        (引擎并发执行)
                                 │
              ┌──────────────────┼──────────────────┐
              ▼                  ▼                  ▼
           SUCCESS             FAIL               WAIT
         (标记完成)          (错误信息)          (稍后重试)
              │                  │                  │
              │                  ▼                  │
              │        ┌──────────────┐            │
              │        │ 告警阈值判断   │            │
              │        │ (模运算控制    │            │
              │        │  推送频率)    │            │
              │        └──────┬───────┘            │
              │               ▼                    │
              │        NotifyChannel               │
              │        (企微/飞书/邮件)              │
              │                                    │
              ▼                                    ▼
        afterExecute                        下次 cron 触发
        (编排下一跳:                         重新拉取执行
         save下一条sync_task)
```

---

## 使用方式

> 引擎已实现 Spring Boot 自动装配，接入方零 Java Config 即可使用。

### 1. 引入依赖

```xml
<!-- 核心引擎 + 自动装配 -->
<dependency>
    <groupId>com.cloud</groupId>
    <artifactId>sync-task-engine-spring-boot-autoconfigure</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- xxl-job 适配器（如果使用 xxl-job） -->
<dependency>
    <groupId>com.cloud</groupId>
    <artifactId>sync-task-engine-xxljob-adapter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置参数解析

**方式一：配置属性（最简）**

```yaml
sync-task:
  engine:
    param-class: com.example.SyncTaskJobParamConfig  # SyncTaskParam 实现类
```

**方式二：自定义 ParamParser（复杂场景）**

```java
@Component
public class MyParamParser implements SyncTaskParamParser {
    @Override
    public SyncTaskEngine.SyncTaskParamSupplier parse(String rawJson) {
        return json -> JSON.parseObject(json, MyParamConfig.class);
    }
}
```

### 3. 实现 Handler

```java
@Component
public class MySyncTaskHandler implements SyncTaskHandler<SyncTaskBO, SyncTaskContext> {
    @Override
    public String getHandlerKey() {
        return "1-1-9"; // orderType-syncTaskType-syncSystem
    }

    @Override
    public List<SyncTaskBO> fetchTasks(SyncTaskParam param) {
        return syncTaskMapper.findPending(param);
    }

    @Override
    public void batchExecute(List<SyncTaskBO> tasks, SyncTaskParam param) {
        // 并发执行、状态管理
    }

    @Override
    public boolean execute(SyncTaskContext context, SyncTaskParam param) {
        // 调用外部系统接口
        return true;
    }

    @Override
    public String formatMessage(SyncTaskContext context, String message) {
        return "[" + context.getOrderCode() + "] " + message;
    }
}
```

### 4. 可选：实现 SPI 扩展

```java
@Component
public class MyValidator implements SyncTaskParamValidator {
    @Override
    public void validate(SyncTaskParam param) throws ValidationException {
        if (param.getOrderType() == null) {
            throw new ValidationException("orderType 不能为空");
        }
    }
}

@Component
public class MyNotifyChannel implements NotifyChannel {
    @Override
    public boolean sendMessage(String content, NotifyLevel level) {
        // 发送企微/飞书/钉钉消息
        return true;
    }
}

@Component
public class MyTaskArchiveService implements TaskArchiveService<SyncTaskBO> {
    @Override
    public int archiveSuccessTasks(Date beforeTime) {
        return mapper.moveToArchive(beforeTime, "SUCCESS");
    }

    @Override
    public int archiveDeadTasks(Date beforeTime) {
        return mapper.moveToArchive(beforeTime, "DEAD");
    }
}
```

> SPI Bean 只需加 `@Component`，引擎通过 `@AutoConfiguration` 自动发现注入。无需手写 `SyncTaskEngine.Builder` 或 `@Configuration` 类。

---

## 模块结构

```
sync-task-engine/
├── pom.xml                                     # 父 POM
├── sync-task-engine-core/                      # 引擎核心（零框架依赖）
│   └── src/main/java/com/cloud/sync/task/engine/
│       ├── spi/                                # 8 个 SPI 接口
│       │   ├── SyncTaskHandler.java            # 同步任务处理器（业务实现）
│       │   ├── SyncTaskParam.java              # 任务调度参数
│       │   ├── SyncTaskParamParser.java        # 参数解析器（JSON → SyncTaskParam）
│       │   ├── SyncTaskParamValidator.java     # 参数校验器
│       │   ├── TaskFetchFilter.java            # 任务拉取过滤器
│       │   ├── TaskStore.java                  # 任务存储抽象
│       │   ├── NotifyChannel.java              # 通知渠道
│       │   ├── LockService.java                # 分布式锁
│       │   ├── TaskLifecycleListener.java      # 生命周期监听器
│       │   ├── TaskRejectedCallback.java       # 拒绝策略回调
│       │   └── TaskArchiveService.java         # 历史任务归档
│       ├── registry/
│       │   └── SyncTaskHandlerRegistry.java
│       ├── executor/
│       │   ├── SyncTaskEngine.java             # 完整编排引擎（Builder 模式）
│       │   └── SchedulerAdapter.java
│       └── util/
│           └── JobLogUtil.java
│
├── sync-task-engine-spring-boot-autoconfigure/  # 自动装配
│   └── src/main/
│       ├── java/.../autoconfigure/
│       │   ├── SyncTaskEngineAutoConfiguration.java  # 自动装配入口
│       │   ├── SyncTaskEngineProperties.java         # 配置属性
│       │   └── JsonSyncTaskParamParser.java          # 默认 JSON 解析器
│       └── resources/META-INF/spring/spring.factories
│
└── sync-task-engine-xxljob-adapter/             # xxl-job 适配器
    └── src/main/
        ├── java/.../adapter/xxljob/
        │   ├── XxlJobSchedulerAdapter.java           # 调度适配器实现
        │   ├── XxlJobSyncTaskLauncher.java           # @XxlJob 桥接器
        │   └── XxlJobAutoConfiguration.java          # 自动装配
        └── resources/META-INF/spring/spring.factories
```

---

## 版本

**1.0.0-SNAPSHOT** — 初始版本，从 cloud-order SyncTaskJob (2024.08) 和 cloud-wms SyncTaskJob (2025.08) 合并抽离。

## 作者

- 架构设计与核心实现：xuh (cloud-order)
- WMS 适配验证：cw (cloud-wms)
- 引擎组件化抽离：2026.07
