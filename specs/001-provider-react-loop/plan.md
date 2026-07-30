# Implementation Plan: Provider 抽象 + ReAct 工具调用对话

**Branch**: `001-provider-react-loop` | **Date**: 2026-07-30 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-provider-react-loop/spec.md`

## Summary

让 `oryxos chat` 支持多轮对话：用户发一句话，LLM 自主判断是否调用工具（如查天气），OryxOS 执行工具、
把结果回填后继续推理，直到给出最终回答。技术上由**自实现的 ReAct 循环**驱动，LLM 调用经 **Provider
抽象**（DeepSeek + 智谱 GLM 两家，显式映射），一个受**白名单沙箱**约束的**通用 `http_get`** 工具连接
免 key 的 open-meteo 公开 API（天气逻辑以 Agent 的 `AGENT.md` **零代码 skill** 配置，不写专用天气工具）；
本特性会话保持在进程内存，审计以结构化日志记录（SQLite 落库留待存储特性）。

## Technical Context

**Language/Version**: Java 21（虚拟线程）

**Primary Dependencies**: Spring Boot 3.4.1；Spring AI Alibaba（**仅**用于 LLM Provider 协议转换 +
`@Tool`/工具 JSON Schema 生成，**禁用其自动 tool 执行**）；Picocli（CLI）；Jackson（JSON）；
自实现 `ReActLoop`（不依赖 Spring AI 的 Agent 抽象）。

**Storage**: 本特性 N/A —— 会话历史保持在**进程内存**；审计以**结构化日志**（Logback JSON）记录；
SQLite 持久化与审计表属后续存储特性（US-5）。

**Testing**: JUnit 5（`spring-boot-starter-test`）做单元测（`ReActLoop` 循环控制与终止、
`WhitelistSandbox` 白名单、`ProviderService` 显式映射选择）；端到端验收走 `quickstart.md`（真实调用
两家 Provider + open-meteo）。

**Target Platform**: Linux（JDK 21 JVM），单可执行 fat JAR，`oryxos chat` 交互式命令行。

**Project Type**: CLI + 库（Agent OS 运行时内核）。本特性交付"命令行多轮工具调用对话"这一端到端切片。

**Performance Goals**: 本特性无独立性能指标（LLM 调用延迟取决于 Provider，不在 OryxOS 控制范围）；
系统级目标见需求文档 §8（Session 创建 P99 ≤ 200ms、内部转发开销 ≤ 50ms），本特性不做压测。

**Constraints**: 同步阻塞 + Java 21 虚拟线程，**不引入** Reactor/WebFlux/`CompletableFuture`；工具外呼
MUST 过白名单沙箱；凭证经环境变量 `${...}`；最大工具调用迭代默认 10；上下文超限保留近期、丢弃最早。

**Scale/Scope**: 单机、单个或少量并发对话；覆盖 spec 的 US-1（工具调用闭环）+ US-2（多轮上下文）+
US-3（模型无关）；触及模块 `oryxos-core`、`oryxos-provider`、`oryxos-tool`、`oryxos-channel-cli`、
`oryxos-cli`、`oryxos-boot`。

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| 宪法原则 | 本特性是否合规 | 说明 |
|---|---|---|
| I 底座优先，运行时内核先行 | ✅ | 只做对接 LLM + ReAct（运行时内核），不含任何治理层能力 |
| II 自实现 ReAct Loop（不可协商） | ✅ **GATE** | `ReActLoop` 自实现，禁止使用 Spring AI 的 Agent 抽象/自动执行 |
| III Spring AI 边界：禁用自动 Tool 执行（不可协商） | ✅ **GATE** | 只用 `ChatModel.call(Prompt)` 做协议转换 + 工具 schema；Tool 由 `ToolExecutor` 调度 |
| IV Provider 显式映射 | ✅ **GATE** | `ProviderService` 维护 `deepseek`/`zhipu` → `ChatModel` 显式映射，禁类型扫描 |
| V 一个目录=一个 Agent；AGENT.md 归 ContextLoader | ✅ | 用最简 `AGENT.md`（含 provider/model/tools）由 `ContextLoader` 注入正文；不进 `ToolRegistry` |
| VI 安全与可审计从第一天起 | ✅ | `WhitelistSandbox` 域名白名单；凭证走环境变量；审计=结构化日志（本特性）不丢信息 |
| VII 同步执行与分阶段克制 | ✅ **GATE** | 全程同步 + 虚拟线程；不引入异步框架；不做 failover/流式 |

**Gate 结论**：全部通过，无违背项 → Complexity Tracking 留空。

## Project Structure

### Documentation (this feature)

```text
specs/001-provider-react-loop/
├── plan.md              # 本文件
├── research.md          # Phase 0：技术决策（Provider 调用/工具协议/天气源/日志）
├── data-model.md        # Phase 1：实体（Session/Message/Profile/OryxTool/ProviderConfig/调用日志）
├── quickstart.md        # Phase 1：端到端验收指南（双 Provider + open-meteo）
├── contracts/           # Phase 1：CLI 命令契约、ProviderService 契约、天气 skill（零代码）
│   ├── cli-chat.md
│   ├── provider-service.md
│   └── weather-skill.md
├── checklists/
│   └── requirements.md  # 规格质量清单（已通过）
└── tasks.md             # Phase 2：/speckit-tasks 生成（本命令不产出）
```

### Source Code (repository root)

复用已有的 9 模块 Maven 骨架，本特性在以下模块内填充实现（不新增模块）：

```text
oryxos-core/src/main/java/com/oryxos/core/
├── engine/            ReActLoop（自实现循环）· PromptBuilder（组装 prompt）· ToolExecutor（调度+沙箱+审计日志）· AgentService（统一入口）
├── session/           Session · Message（role: user/assistant/tool）· SessionManager（内存版）
├── profile/           Profile · AgentLoader.deriveProfile（从 AGENT.md frontmatter）· ContextLoader（正文注入）
└── tool/              OryxTool 接口 · ToolResult

oryxos-provider/src/main/java/com/oryxos/provider/
├── ProviderService            provider name → ChatModel 显式映射；ChatModel.call(Prompt) 直调
├── ProviderConfig             application.yaml 绑定（deepseek/zhipu：base-url/model/api-key 占位）
└── FunctionCallingAdapter     OryxTool ↔ Spring AI 工具定义/协议格式转换（不自动执行）

oryxos-tool/src/main/java/com/oryxos/tool/
├── builtin/HttpTools          http_get（受白名单约束的通用 GET；天气由默认 Agent 的 AGENT.md skill 指导两步调用）
├── sandbox/Sandbox · WhitelistSandbox   域名白名单（放行 open-meteo）
└── ToolRegistry               注册可用 Tool，按 Profile 过滤

oryxos-channel-cli/src/main/java/com/oryxos/channel/cli/
└── CliChannel                 读 stdin/写 stdout，多轮循环，每轮调 AgentService.process

oryxos-cli/src/main/java/com/oryxos/cli/
└── OryxOsCli                  `oryxos chat [--profile] [--message]` 子命令接 CliChannel（需要 Spring 上下文）

oryxos-boot/src/main/java/com/oryxos/boot/
└── OryxOsApplication          装配上述 Bean（本特性无需数据源）
```

**Structure Decision**：沿用既有 9 模块骨架，本特性只在 `core / provider / tool / channel-cli / cli / boot`
六个模块内落实现，**不新增模块、不动 memory/web/storage**（它们属后续特性）。跨模块契约（`OryxTool`、
`Session`、`Profile`）已在 `oryxos-core`，符合宪法"契约放 core、依赖倒置"。

## Complexity Tracking

> 无宪法违背项，无需填写。
