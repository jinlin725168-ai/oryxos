# Phase 1 Data Model: Provider 抽象 + ReAct 工具调用对话

本特性实体大多为**进程内内存对象**（会话不落库）；`ProviderConfig` 来自 `application.yaml`；审计为
结构化日志事件（非表）。字段命名与未来 SQLite 表对齐，便于后续无损迁移。

## 核心实体

### Session（会话）— 内存

一次对话的上下文容器。

| 字段 | 类型 | 说明 |
|------|------|------|
| `sessionId` | String | 主键；channel + user + profile 联合生成 |
| `profileName` | String | 绑定的 Profile/Agent 名 |
| `channel` | String | 本特性固定为 `cli` |
| `userId` | String | 用户标识（CLI 下可为本地用户） |
| `messages` | List\<Message> | 按时间累积的对话历史 |
| `status` | enum | `active` / `archived`（本特性主要 active） |

- 关系：Session `1 — *` Message。
- 规则：组装 prompt 时保留 system + 最近 `maxHistoryTurns`（默认 20）轮，超出丢弃最早（FR-006）。

### Message（消息）— 内存

| 字段 | 类型 | 说明 |
|------|------|------|
| `role` | enum | `system` / `user` / `assistant` / `tool` |
| `content` | String | 文本内容（tool 角色为工具结果 JSON/文本） |
| `toolCalls` | List\<ToolCall> | 仅 `assistant` 角色可有；模型要求调用的工具 |
| `toolCallId` | String | 仅 `tool` 角色；对应它回应的 ToolCall id |
| `name` | String | 仅 `tool` 角色；被调用的工具名 |

- 规则：`assistant` 消息若含 `toolCalls` 则进入 Act 阶段；`tool` 消息由 `ToolExecutor` 产出后回填。

### ToolCall（工具调用请求）

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | String | 模型分配的调用 id |
| `toolName` | String | 目标工具名（须在 Profile 可用集合内） |
| `argumentsJson` | String | 入参（JSON，符合工具 inputSchema） |

### Profile（由 AGENT.md frontmatter 派生）— 内存

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `name` | String | — | Agent 名 |
| `description` | String | — | 描述 |
| `providerName` | String | — | 对应 `ProviderService` 映射 key（`deepseek`/`zhipu`） |
| `model` | String | — | 模型名 |
| `temperature` | Float | 可空 | 采样温度 |
| `tools` | List\<String> | — | 可用工具名（本特性含 `http_get`） |
| `maxIterations` | int | 10 | ReAct 单条消息最大工具调用轮次（FR-004） |
| `maxHistoryTurns` | int | 20 | 上下文保留轮数（FR-006） |

- 来源：`.oryxos/agents/<name>/AGENT.md` frontmatter，经 `AgentLoader.deriveProfile` 派生；正文由
  `ContextLoader` 注入 system prompt。

### OryxTool（工具抽象）+ ToolResult

```text
OryxTool: getName() · getDescription() · getInputSchema()(JSON Schema) · execute(argsJson) -> ToolResult
ToolResult: { success: boolean, content: String, errorMessage: String?, retryable: boolean }
```

- 本特性实现：`http_get`（受白名单约束的通用 GET）。天气不写专用工具，由默认 Agent 的 `AGENT.md`
  skill 指导 LLM 用 `http_get` 两步查 open-meteo（geocoding→forecast）。

### ProviderConfig（配置，非运行时可变）

| 字段 | 类型 | 说明 |
|------|------|------|
| `name` | String | 映射 key（`deepseek` / `zhipu`） |
| `baseUrl` | String | OpenAI 兼容端点 |
| `model` | String | 默认模型 |
| `apiKey` | String | `${DEEPSEEK_API_KEY}` / `${ZHIPU_API_KEY}` 环境变量占位（FR-012） |

- 关系：`ProviderService` 启动时据此构建 `Map<String, ChatModel>`（宪法 IV 显式映射）。

## 审计日志事件（结构化日志，非表）

字段与未来 `llm_calls` / `tool_invocations` 表对齐（澄清 Q3；宪法 VI）。

**LlmCall 事件**：`sessionId` · `provider` · `model` · `promptTokens` · `completionTokens` ·
`totalTokens` · `durationMs` · `ts`

**ToolInvocation 事件**：`sessionId` · `toolName` · `success` · `errorMessage?` · `durationMs` ·
`inputSummary`（脱敏）· `ts`

- 规则：MUST 不含明文凭证等敏感信息（FR-011/FR-012）。

## 状态流转（ReAct 单条消息处理）

```text
用户消息 → [Reason] 调 LLM
      ├─ 无 toolCalls → 产出 assistant 最终回答 → 结束
      └─ 有 toolCalls → [Act] ToolExecutor 逐个执行（过 Sandbox）→ 追加 tool 消息 → 回到 Reason
达到 maxIterations → 强制结束，返回当前最佳回答（FR-004）
```
