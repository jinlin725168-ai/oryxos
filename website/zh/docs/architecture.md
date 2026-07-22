# 整体架构

OryxOS 是一个 **Spring Boot 3.x** 单体应用，跑在 **JDK 21** 上，基于 **Spring AI Alibaba** 做 LLM 调用，自己实现 **ReAct loop** 作为 Agent 核心。整个 OryxOS 是一个可执行 JAR，单二进制部署。

![OryxOS 整体架构](/architecture.svg)

## 分层视图

从上到下五层，所有边向下流：

1. **接入层**（三触发源）：CLI Channel、Web Service、`AgentScheduler` 定时触发
2. **统一入口**：`AgentService`——三触发源汇入同一条链路，引擎不感知来源
3. **引擎层**：`ReActLoop`、`PromptBuilder`、`ToolExecutor`，是 Agent 的大脑
4. **能力层**：Provider、Memory、Tool，给引擎提供 LLM 调用、上下文、执行能力
5. **基础层**：Session 与审计落 SQLite，Agent 目录 / Bootstrap / Memory 落文件系统

> 一句话：Provider、Memory、Tool 三块能力供养 ReAct 循环这个引擎，引擎跑出的能力经 CLI、Web Service、定时任务三个入口对外提供。

## 七个关键技术决策

| # | 决策 | 选择 |
|---|------|------|
| 1 | ReAct loop | 自实现，不依赖 Spring AI Agent 抽象 |
| 2 | Spring AI 边界 | 只用 Provider 抽象 + 协议转换 + schema 生成，**禁用自动 tool 执行** |
| 3 | 执行模型 | 同步阻塞 + JDK 21 虚拟线程 |
| 4 | Tool 注册 | `@Tool` 注解 + `OryxTool` 抽象层，统一内置与 MCP Tool |
| 5 | HTTP 层 | Spring MVC + 虚拟线程 |
| 6 | Sandbox | 接口先行，核心阶段 `WhitelistSandbox` 应用层白名单 |
| 7 | 持久化 | SQLite + Spring Data JPA + `MEMORY.md`，审计表 day one 写入 |

## 9 个 Maven 模块

| 模块 | 职责 |
|------|------|
| `oryxos-core` | 核心抽象与引擎：`ReActLoop`、`PromptBuilder`、`ToolExecutor`、`AgentService`、`AgentScheduler` |
| `oryxos-provider` | 对接 LLM：`ProviderService`、Function Calling 适配 |
| `oryxos-memory` | 三层记忆：`MemoryService`、`LongTermMemoryStore`、`MemoryTools` |
| `oryxos-tool` | 工具体系：内置 Tool、MCP Client、`ToolRegistry`、`Sandbox` |
| `oryxos-channel-cli` | CLI Channel |
| `oryxos-web` | Web Service：6 个 Controller、统一异常、OpenAPI |
| `oryxos-storage` | SQLite 持久化层 |
| `oryxos-cli` | Picocli 入口、12 个子命令、配置加载 |
| `oryxos-boot` | Spring Boot 启动与依赖聚合 |

模块间靠接口解耦，扩展新增 Channel / Tool 只加新模块，不改核心引擎。

## 数据持久化

核心阶段 SQLite 五张表：`sessions`、`tool_invocations`、`llm_calls`、`scheduled_tasks`、`task_executions`。长期记忆是 `MEMORY.md` 文件，不入库。

> **审计 day one**：`tool_invocations` 和 `llm_calls` 核心阶段就写入落库（不只是日志），让"可审计"这个差异化能力的数据地基从第一天立起来。
