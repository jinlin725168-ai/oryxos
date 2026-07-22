# OryxOS — Claude Code 项目指南

> 本文件是 OryxOS 的工程宪法与上手指南。凡在本仓库工作，先读本文件，再动手。
> 权威来源是 `docs/` 下四份文档，本文件是它们的浓缩执行版。冲突时以 `docs/TechnicalSolution.md`（最新技术方案）为准。

---

## 1. 这是什么

OryxOS 是**用 Java 实现的、面向严监管企业的私有化 Agent OS（智能体操作系统底座）**。装在企业自己的 K8s/服务器上，作为统一底座跑各种业务 Agent（运维、客服、HR、销售、知识管理），共享一套「渠道接入 + 模型路由 + 工具调用 + 记忆 + 沙箱」能力。数据不出企业、不锁云生态。

- **填补的空白**：开源 Agent OS 只有 OpenClaw（Node.js）、Hermes（Python），Java 生态在这一层完全空白。
- **交付分两段**：核心阶段只做**运行时内核**（对齐业界开源基础层）；企业级治理（多租户/SSO/审计查询/Tool Policy）留扩展与社区阶段。**核心阶段是地基，不是完备的企业级产品——文档不许包装成后者。**
- **边界**：只做运行时（承载常驻 Agent），**不做**可视化 workflow 编排（那是 Dify/Coze 的活，可跑在 OryxOS 之上）。

## 2. 当前状态

**纯 greenfield，尚无一行代码。** 仓库目前只有 `docs/`。主体开发用 Spec-Kit 从零构建（见第 8 节）。

---

## 3. 技术栈（不可协商）

`JDK 21` + `Spring Boot 3.x` + `Spring AI Alibaba` + 自实现 ReAct loop + `SQLite`(Spring Data JPA) + `MEMORY.md` 文件 + `Picocli` 命令行。单体应用，打成一个可执行 fat JAR，单二进制部署。

- 并发模型：**同步阻塞 + JDK 21 虚拟线程**，不用响应式编程。
- HTTP 层：Spring MVC + 虚拟线程。
- 扩展阶段才引入：GraalVM Native Image、向量检索、Prometheus 指标。

---

## 4. 🔴 宪法级约束（最容易被 AI 写错，逐条守住）

| # | 约束 | 说明 |
|---|------|------|
| 1 | **Spring AI 只用一半** | 只用它的 Provider 抽象、协议转换、`@Tool` schema 生成。**必须禁用 Spring AI 的自动 tool 执行**——tool 的调度/执行完全由自实现的 `ReActLoop` + `ToolExecutor` 掌控。若发现 tool 被调用两次，第一时间查这里。**全项目头号坑。** |
| 2 | **自实现 ReAct loop** | 核心循环约数十行 Java，**不依赖** Spring AI 的 Agent 抽象。 |
| 3 | **Provider 用显式 name 映射** | 多个 `ChatModel` Bean 类型相同，**不能靠类型扫描**区分 deepseek/kimi/qwen。必须维护 provider-name → `ChatModel` 的显式映射表。 |
| 4 | **审计表 day one 落库** | `tool_invocations`、`llm_calls` 核心阶段就**写入 SQLite**，不是只写日志（查询接口可留扩展阶段）。可审计是差异化卖点，数据地基不能后补。 |
| 5 | **一个目录 = 一个 Agent** | Agent 定义 = 一个目录 `.oryxos/agents/<name>/`（`AGENT.md` + 可选 `skills/` `scripts/` `REFERENCE.md`）。借 Anthropic Agent Skills 的**形态**，但定义的是 Agent，不做跨 Agent 共享能力库、无 `use_skill`、无全局索引。**不再写独立的 Profile YAML**（`AgentLoader.deriveProfile` 从 `AGENT.md` frontmatter 派生 Profile）。 |
| 6 | **AGENT.md 不是 Tool** | `AGENT.md` 正文由 `ContextLoader` 注入 system prompt（与 Bootstrap 同层），归 `oryxos-core`，**不放 Tool 模块**。子指令/脚本经底座既有 `read_file`/`shell` 按需读取（渐进式披露），不新造机制。 |
| 7 | **接口先行** | `Sandbox`、`LongTermMemoryStore`、`NotifyChannelAdapter` 都先定中立接口，核心阶段各挂一档实现；升级只新增实现类、不改接口/调用方。接口签名里不出现某一档实现特有的词（如"白名单""容器镜像"）。 |
| 8 | **不用 SecurityManager** | 它在 JDK 17 起废弃、JDK 21 已不可用。沙箱核心阶段只做应用层白名单校验（`WhitelistSandbox`）。 |
| 9 | **多 Agent 并存** | 同一实例上多个 Agent 同时可用——这是"OS"在核心阶段的最小体现。 |

---

## 5. 架构：9 个 Maven 模块

| 模块 | 职责 |
|------|------|
| `oryxos-core` | 核心抽象：`OryxTool`/`Session`/`Profile` 接口、`ReActLoop`、`PromptBuilder`、`ToolExecutor`、`AgentService`（三触发源统一入口）、`AgentLoader`、`ContextLoader`、`AgentScheduler`（定时触发） |
| `oryxos-provider` | 能力一：`ProviderService`、Function Calling 适配、provider-name→`ChatModel` 显式映射 |
| `oryxos-memory` | 能力三：`MemoryService`（三层统一门面）、`LongTermMemoryStore`、`MemoryTools` |
| `oryxos-tool` | 能力四（三合一）：内置 Tool、`McpClientService`/`McpToolAdapter`、`ToolRegistry`、`Sandbox`+`WhitelistSandbox`、`NotifyChannelAdapter`+`WebhookNotifyAdapter` |
| `oryxos-channel-cli` | CLI Channel：`CliChannel`、`oryxos chat` |
| `oryxos-web` | 能力五：`WebServer`、6 个 `ApiController`、`GlobalExceptionHandler`、OpenAPI |
| `oryxos-storage` | 持久化：SQLite、各 Repository |
| `oryxos-cli` | Picocli 主入口、12 个子命令、`ConfigLoader` |
| `oryxos-boot` | Spring Boot 启动、自动配置、依赖聚合 |

模块间靠接口解耦。扩展新增 Channel/Tool 只加新模块，不改 `oryxos-core`；所有 IM Channel 底层都调 `oryxos-web` 的 Agent 接口，不重复实现 Agent 逻辑。

### 五大核心能力（全项目骨架）
1. **对接 LLM**（Provider 抽象）  2. **ReAct 循环**（Agent 大脑）  3. **Memory 三层记忆**（核心阶段做会话+长期两层）  4. **Plugin Tool + 内置工具集**  5. **Web Service**（对外唯一门面）

> 关系：Provider / Memory / Tool 三块能力供养 ReAct 循环这个引擎；引擎经 CLI、Web Service、`AgentScheduler`（定时）三个入口对外，三者都汇入同一个 `AgentService`。

---

## 6. 关键约定速查

**内置 Tool（9 个）**：`read_file` `write_file` `list_dir`（`FileTools`）、`shell`（`ShellTools`）、`http_get` `http_post`（`HttpTools`）、`save_memory` `recall_memory`（`MemoryTools`）、`notify`（`NotifyTools`）。文件/Shell/HTTP 执行前先调 `Sandbox.enforce(...)`。

**Plugin Tool 三档（能低不高）**：① 零代码 `AGENT.md` 目录 + 复用 MCP（主推）② 轻代码自写 MCP server ③ 重代码 Java `@Tool` Bean。

**Web Service 核心 10 端点**：`POST /sessions`、`POST /sessions/{id}/messages`、`GET /sessions/{id}`、`DELETE /sessions/{id}`、`POST /agents/{name}/invoke`、`GET /profiles`、`GET /memory`、`GET /tools`、`GET /health`、`GET /info`（前缀 `/api/v1`）。核心阶段**无认证**（假设内网）、无 SSE/WebSocket/RBAC。

**SQLite 五张表**：`sessions`、`tool_invocations`、`llm_calls`、`scheduled_tasks`、`task_executions`。长期记忆是文件 `.oryxos/memory/MEMORY.md`，不入库。
> ⚠️ SQLite `ALTER TABLE` 弱，别依赖 `hibernate.ddl-auto=update` 做表结构演进；表结构变更手写建表脚本或上 Flyway/Liquibase。

**工作区 `.oryxos/`**：`agents/`（每目录一个 Agent）、`memory/MEMORY.md`、`mcp_servers.yaml`、`sessions/`、`logs/`、`AGENTS.md`/`SOUL.md`/`USER.md`（Bootstrap，`ContextLoader` 全量注入 prompt）、`oryxos.db`。

**密钥**：Profile 里用 `${ENV_VAR}` 占位，`ConfigLoader` 从环境变量解析，**不明文写死**。

---

## 7. 命令

```bash
# 构建
mvn clean package          # 生成 fat JAR

# 运行时 12 个 CLI 子命令
oryxos init                                    # 初始化 .oryxos/ 工作区
oryxos status
oryxos chat [--profile <name>] [--message "…"] # 交互对话（主要调试入口）
oryxos serve                                   # 启动 REST API（默认 8080）
oryxos gateway                                 # 多渠道守护进程
oryxos profile list | create <name> | show <name> | delete <name>
oryxos provider list
oryxos tool list
oryxos session list
```
不需要 LLM 的命令（`init`、`profile list`）走纯文件操作快速启动；需要 LLM 的（`chat`/`serve`/`gateway`）才起 Spring 上下文。

---

## 8. 开发流程：Spec-Kit + 5 个 User Story

主体开发用 **Spec-Kit**（把 `docs/` 喂进去，不重写）：`docs/DemandAnalysis.md`→`specify`，`docs/TechnicalSolution.md`→`plan`，宪法从需求第 3 章 + 技术方案 1.1 提炼进 `constitution.md`。

**5 个 user story 按依赖顺序推进**（不按重要性；US-5 排最后是因依赖，不是不重要）：

```
US-1 对接LLM → US-2 ReAct → ┌ US-3 Memory ┐ → US-5 Web Service
                            └ US-4 Tool    ┘   (US-3∥US-4 可并行)
```

- 每个 user story 完成后**必跑 `/speckit.analyze`** 查一致性，不能省。
- AI 跑偏 constitution 就让它重读 constitution 改正（重点盯第 4 节九条）。
- `constitution.md` 一旦定下主体开发期不改；不允许 AI 自行修改。
- 增量阶段（扩展/修 bug/加 Plugin）切换到手动提示词 + Claude Code，不走完整 Spec-Kit。

---

## 9. 验收 Demo（发布硬条件：3 个每日自动运行的端到端 Demo）

> 以技术方案第 12 章为准。三个 Demo 各演一种 Agent 目录丰富度，横向串起全部五大能力 + 定时触发。
> ⚠️ 注意：`docs/AiProgrammingGuide.md` 里仍引用旧的"5 个 Demo"，那是过时表述，以下 3 个才是权威。

| Demo | Agent 形态 | 覆盖能力 |
|------|-----------|---------|
| **每日天气** | 光杆 `AGENT.md` | 能力一+二 + 内置 HTTP Tool + `NotifyTools` + Sandbox + 定时 |
| **每日科技日报** | `AGENT.md` + `skills/` 子指令 | Plugin 方式二(MCP) + Memory + 子指令按需 `read_file` + 定时 |
| **每日 GitHub 日报** | `AGENT.md` + `scripts/` 脚本 | `shell` 跑捆绑脚本(信任边界) + ReAct + Memory + 定时 |

每个 Demo 既能"钟推"（`AgentScheduler` 到点触发）也能"人推"（`oryxos chat` / `POST /agents/{name}/invoke`）手动补跑，验证同一条 `AgentService` 链路。

---

## 10. 提交前自检清单

- [ ] 没启用 Spring AI 自动 tool 执行（约束 1）
- [ ] ReAct 是自实现，没用 Spring AI Agent 抽象（约束 2）
- [ ] Provider 用显式 name 映射，不是类型扫描（约束 3）
- [ ] `tool_invocations` / `llm_calls` 有落库（约束 4）
- [ ] Tool 相关代码都在**一个** `oryxos-tool` 模块，没被拆散（第 5 节）
- [ ] `AGENT.md`/`AgentLoader` 归 `oryxos-core` 的 `ContextLoader`，没当 Tool（约束 6）
- [ ] 用的是 JDK 21，没用被废弃的 API（如 SecurityManager）
- [ ] 无硬编码密钥，敏感配置走 `${ENV_VAR}`

---

## 11. 文档索引

| 文档 | 回答 | 内容 |
|------|------|------|
| `docs/IndustryResearch.md` | Why | 业界格局、Java 生态缺位、OryxOS 定位 |
| `docs/DemandAnalysis.md` | What | 五大能力、术语、场景、验收标准（**功能权威源**） |
| `docs/TechnicalSolution.md` | How | 9 模块技术设计、7 决策、3 Demo（**技术权威源**） |
| `docs/AiProgrammingGuide.md` | How to Build | Spec-Kit 流程、5 user story 拆解（注意其 Demo 数量已过时） |
