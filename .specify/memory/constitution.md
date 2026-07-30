<!--
Sync Impact Report
- Version change: (未版本化模板) → 1.0.0
- Bump rationale: 首次批准，从模板落地为具体宪法（新增全部核心原则与治理章节），采用初始版本 1.0.0
- Principles (初始，全部新增):
    I.   底座优先，运行时内核先行
    II.  自实现 ReAct Loop（不可协商）
    III. Spring AI 边界：禁用自动 Tool 执行（不可协商）
    IV.  Provider 显式映射
    V.   一个目录 = 一个 Agent；AGENT.md 是上下文而非 Tool
    VI.  安全与可审计从第一天起
    VII. 同步执行与分阶段克制
- Added sections: Core Principles(7)；技术栈与架构约束；开发工作流；Governance
- Removed sections: 无
- Templates status:
    ✅ .specify/templates/plan-template.md   — 运行时读取宪法，无需改动
    ✅ .specify/templates/spec-template.md   — 运行时读取宪法，无需改动
    ✅ .specify/templates/tasks-template.md  — 运行时读取宪法，无需改动
- Deferred TODOs: 无（Ratified 采用今日为首次批准日；如需改为更早的立项日，后续修订）
-->

# OryxOS Constitution

OryxOS 是用 Java 实现、面向企业场景的 Agent OS（智能体操作系统底座）：装在企业自己的 K8s 或
服务器上，作为统一底座运行多个业务 Agent，共享渠道接入、模型路由、工具调用、记忆、沙箱能力，
数据不出企业、不锁云生态。本宪法定义所有代码必须遵守的非协商原则，来源为
`docs/DemandAnalysis.md`、`docs/TechnicalSolution.md`、`docs/AiProgrammingGuide.md`。

## Core Principles

### I. 底座优先，运行时内核先行

交付的核心不是某个强大的业务 Agent，而是让任意 Agent 可靠运行的底座。核心阶段 MUST 只交付
**运行时内核**——五大核心能力（对接 LLM、ReAct 循环、Memory、Plugin Tool、Web Service）加定时
触发；企业级治理层（多租户、SSO、完整审计查询、Tool Policy）MUST 推迟到扩展阶段。任何特性在
进入实现前 MUST 归类为"运行时内核"或明确标注"扩展阶段"，不得把治理层能力混入核心阶段范围。

Rationale：核心阶段是地基、企业级治理是终局；范围收紧才能在有限投入内交付可演示的最小完整内核。

### II. 自实现 ReAct Loop（不可协商）

`ReActLoop` MUST 由项目自己实现（约数十行 Java），MUST NOT 使用 Spring AI 的 Agent 抽象或其
自动工具执行链路。循环的迭代控制、消息累积、最大迭代次数（默认 10）MUST 完全由 OryxOS 掌控。

Rationale：完整掌握 Agent 工作机制，保留未来定制循环行为的空间；这是 OryxOS 最核心的一段代码。

### III. Spring AI 边界：禁用自动 Tool 执行（不可协商）

Spring AI / Spring AI Alibaba 在 OryxOS 中 MUST 只用于两件事：(1) LLM Provider 协议转换；
(2) `@Tool` 注解的 JSON Schema 生成。MUST 禁用 Spring AI 的自动 Tool 执行——Tool 的调度与执行
MUST 完全由 `ReActLoop` + `ToolExecutor` 控制。禁止出现 `chatClient.prompt().tools(...).call()`
这类自动执行写法。

Rationale：这是最容易被写错的一条；启用自动执行会导致 Tool 被调用两次、循环失控。

### IV. Provider 显式映射

多 Provider 并存时，MUST NOT 依赖扫描 Spring 容器里的 `ChatModel` Bean 类型来区分 Provider
（Bean 类型相同会歧义）。MUST 维护 `provider name → ChatModel` 的显式映射表，Profile 通过
provider name 引用。

Rationale：类型扫描在多 Provider 下必然路由错乱；显式映射是唯一可靠方式。

### V. 一个目录 = 一个 Agent；AGENT.md 是上下文而非 Tool

一个业务 Agent MUST 由一个目录 `.oryxos/agents/<name>/` 定义：`AGENT.md`（frontmatter = 该
Agent 的 profile；正文 = 任务指令）+ 可选 `skills/`、`scripts/`、`REFERENCE.md`。
`AgentLoader.deriveProfile()` MUST 把 frontmatter 派生成 `Profile`（`.oryxos/profiles/` 取消）。
`AGENT.md` 正文 MUST 由 `ContextLoader`/`PromptBuilder` 注入 system prompt，MUST NOT 被当作可
执行 Tool 注册进 `ToolRegistry` 或放入 `oryxos-tool` 模块；子指令/脚本 MUST 经底座既有的
`read_file`/`shell` 按需取用（渐进式披露），不得新造能力库、`use_skill` 或全局索引。

Rationale：Agent 是配置出来的、不是写代码写出来的；上下文与工具是两层，混淆会导致注册与执行错误。

### VI. 安全与可审计从第一天起

安全 MUST 是地基而非补丁：(1) 沙箱 MUST 通过应用层白名单实现（文件路径 / Shell 命令首 token /
HTTP 域名），MUST NOT 使用已废弃的 Java `SecurityManager`；(2) 敏感凭证（API key、Tool 凭证）
MUST 通过环境变量 `${ENV_VAR}` 注入，MUST NOT 明文写入配置；(3) 审计表 `tool_invocations` 与
`llm_calls` MUST 在核心阶段就写入 SQLite（可以暂不做查询接口，但写入不得省略）。

Rationale：可审计是 OryxOS 面向严监管企业的核心差异化；审计数据地基必须 day one 立起来，否则后期
需从日志反解析返工。

### VII. 同步执行与分阶段克制

核心阶段 MUST 采用同步阻塞执行模型，配合 Java 21 Virtual Thread 处理并发，MUST NOT 引入
Reactor / WebFlux / `CompletableFuture` 等异步编程模型（SSE 流式响应属扩展阶段）。功能扩展
SHOULD 信号驱动——出现真实需求信号（如要跑不可信代码、要多租户）才升级，接口先行、实现后补。

Rationale：同步直观 + 虚拟线程已足以单机撑高并发；提前引入异步与重隔离只增加复杂度、无实际收益。

## 技术栈与架构约束

- 语言/运行时 MUST 为 **Java 21**（虚拟线程）；框架 MUST 为 **Spring Boot 3.x**；单体应用，
  产出单个可执行 fat JAR，单二进制部署。
- LLM 调用基于 **Spring AI Alibaba**（边界见原则 III）；HTTP 层 **Spring MVC + 虚拟线程**；
  命令行 **Picocli**；YAML 解析 **SnakeYAML**；持久化 **SQLite + Spring Data JPA** 加 `MEMORY.md`
  文件；日志 **Logback + SLF4J**（结构化 JSON）。核心阶段 MUST NOT 引入向量数据库。
- 构建为 **Maven 多模块**，基线为 9 个模块（core / provider / memory / tool / channel-cli /
  web / storage / cli / boot）。内置 Tool 与 MCP Client MUST 合并在单一 `oryxos-tool` 模块，
  MUST NOT 拆成多个 Tool 模块。跨模块契约（接口 + 值对象）放 `oryxos-core`，禁止模块间循环依赖。
- 模块划分 MAY 随能力域按需演进（新建/改名/调整边界），但 MUST 在对应特性的 plan 中声明理由，
  并同步更新 `CLAUDE.md` 与 `docs/TechnicalSolution.md` §10。
- Plugin Tool 三档接入，SHOULD 遵循"能用零代码（AGENT.md + MCP）就不用轻代码（自写 MCP server），
  能用轻代码就不用重代码（Java `@Tool` Bean）"。
- Web Service 核心阶段 MUST 覆盖 10 个 REST 端点（前缀 `/api/v1`），核心阶段不做认证 / SSE /
  WebSocket / RBAC / 限流。

## 开发工作流

- 主体开发 MUST 走 Spec-Kit 流程：`/speckit.constitution` → `/speckit.specify` → `/speckit.plan`
  → `/speckit.tasks` → `/speckit.implement`；输入为既有四份文档，不重写。
- 实现 MUST 按 5 个 user story 的依赖顺序推进：**US-1 → US-2 →（US-3 ∥ US-4）→ US-5**。
- 每个 user story 完成后 MUST 跑 `/speckit.analyze` 做跨产物一致性检查，并 MUST 有一个可演示成果 +
  一次 git commit 标记稳定点。
- 验收以 `docs/TechnicalSolution.md` §12 的 **3 个每日自动运行的端到端 Demo** 为准（每日天气 /
  每日科技日报 / 每日 GitHub 日报）。`docs/AiProgrammingGuide.md` 中"5 个 Demo"的表述为旧口径，
  已被 §12 取代，MUST NOT 据其扩展验收范围。
- AI agent 生成的代码若违反本宪法，MUST 立即让其重读宪法并改正（重点盯原则 II / III / IV / V）。

## Governance

- 本宪法 MUST 优先于其他一切实践约定；与 `CLAUDE.md`、文档冲突时以本宪法与
  `docs/TechnicalSolution.md`（最新技术方案）为准。
- `CLAUDE.md` 是每次会话加载的运行时开发指南，MUST 与本宪法保持一致。
- 修订 MUST 记录变更、按语义化版本升级（MAJOR：删除/重定义原则；MINOR：新增或实质扩充；
  PATCH：措辞澄清），并更新顶部 Sync Impact Report。
- 主体开发期内 MUST NOT 由 AI agent 自行修改本宪法；如发现某条原则不对，MUST 停下与人讨论后再改。
- 所有 plan / tasks / implement 产出 MUST 可追溯到对应原则；复杂度偏离 MUST 在 plan 中说明理由。

**Version**: 1.0.0 | **Ratified**: 2026-07-30 | **Last Amended**: 2026-07-30
