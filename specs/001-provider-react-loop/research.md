# Phase 0 Research: Provider 抽象 + ReAct 工具调用对话

本阶段解决实现前的技术未决点。每项按 Decision / Rationale / Alternatives 记录。

## R1. 如何调用 LLM 且禁用 Spring AI 的自动 Tool 执行（宪法 III）

- **Decision**：直接用底层 `ChatModel.call(Prompt)` 传入消息列表 + 工具定义（`ToolCallback`/工具
  metadata 仅作为**声明**传给模型），从返回的 `ChatResponse` 里**自己解析** `toolCalls`，交由
  `ToolExecutor` 执行后，把结果作为一条 tool 消息追加进下一轮 `Prompt`。**不使用** `ChatClient`
  的 `.tools(...).call()` 自动执行链路。
- **Rationale**：`ChatClient` 的高阶 API 会在框架内部自动执行工具并回灌，导致 Tool 被调两次、循环
  失控；`ChatModel.call` 是最底层入口，只做协议转换与一次模型往返，Tool 的调度权完整留在 `ReActLoop`。
- **Alternatives**：① `ChatClient.prompt().tools().call()`（被宪法禁止）；② 完全手写各家 HTTP/JSON
  协议（放弃 Spring AI 的多厂商协议转换，重复造轮子，拒绝）。

## R2. Provider 显式映射（宪法 IV）

- **Decision**：`ProviderConfig` 从 `application.yaml` 读取每个 provider 的 `name/base-url/model/
  api-key`，`ProviderService` 启动时建立 `Map<String, ChatModel>`（key = provider name，如
  `deepseek`、`zhipu`）。Profile/AGENT.md 的 `provider.name` 作为 key 取用。
- **Rationale**：多 Provider 的 `ChatModel` Bean 类型相同，类型扫描无法区分；显式 name→实例映射是
  唯一可靠方式。两家都兼容 OpenAI 协议，可用 Spring AI 的 OpenAI 兼容 `ChatModel` 配不同 base-url/
  model 实例化。
- **Alternatives**：靠 `@Qualifier`/Bean name 扫描（宪法明确禁止，歧义）。

## R3. 工具调用协议：OpenAI 兼容 Function Calling

- **Decision**：`OryxTool.getInputSchema()` 返回 JSON Schema；`FunctionCallingAdapter` 把 OryxTool
  转成 Spring AI 的工具定义（function name + description + JSON Schema 参数），随 `Prompt` 传给模型。
  DeepSeek 与 智谱 GLM 均支持 OpenAI 风格的 `tools`/`tool_calls`，由 Spring AI 吸收各家细节差异。
- **Rationale**：OpenAI 兼容协议是事实标准；两家目标 Provider 都实现它，一套适配即可覆盖。
- **Alternatives**：各家私有 function-calling 格式分别适配（成本高、无必要）。

## R4. 天气数据源：open-meteo（免 key）

- **Decision**：用 **open-meteo** 两个免 key 端点：`geocoding-api.open-meteo.com`（城市名→经纬度）
  与 `api.open-meteo.com`（按经纬度取当日天气）。两域名加入 HTTP 白名单。天气能力以**零代码**落地：
  默认 Agent 的 `AGENT.md` skill 指导 LLM 用通用 `http_get` 两步调用（geocoding→forecast），
  **不写专用 Java 天气工具**（宪法 V + 工具三档取最低）。
- **Rationale**：免注册 key、免费、稳定、CORS 无关（服务端调用）；验收判定"是否成功获取并综合天气"，
  不锁死温度数值，可稳定复现。
- **Alternatives**：需 key 的和风/OpenWeather（增加凭证管理，验收更重）；本地 mock（不验证真实外呼）。

## R5. 上下文长度管理

- **Decision**：`SessionManager` 保留 system prompt + 最近 `maxHistoryTurns`（默认 20）轮对话，超出
  部分**直接截断丢弃最早**；同时 `ReActLoop` 以 `maxIterations`（默认 10）限制单条消息内的工具调用轮次。
- **Rationale**：核心阶段策略从简（宪法 VII 分阶段克制）；总结压缩属扩展阶段。
- **Alternatives**：按 token 精确计数截断 / 总结压缩（复杂度高，推迟）。

## R6. 审计与日志（本特性=结构化日志）

- **Decision**：每次 LLM 调用记录 `provider/model/prompt_tokens/completion_tokens/total_tokens/
  duration_ms`；每次工具调用记录 `tool_name/success/error/duration_ms/input摘要`。用 Logback 输出
  **结构化 JSON**（一行一事件），字段与未来的 `llm_calls`/`tool_invocations` 表对齐，便于无损迁移。
  凭证等敏感信息 MUST 不入日志。
- **Rationale**：本特性未引入 SQLite（澄清 Q3）；结构化日志 day one 保证审计信息不丢、字段对齐即可
  平滑落库。
- **Alternatives**：本特性直接建 SQLite 两表（撑大范围、打乱 US 依赖顺序，已否）。

## R7. AGENT.md 最简加载（宪法 V 奠基）

- **Decision**：本特性支持从 `.oryxos/agents/<name>/AGENT.md` 读取 frontmatter（`provider/model/
  tools`）+ 正文；`AgentLoader.deriveProfile` 产出 `Profile`，`ContextLoader` 把正文注入 system
  prompt。默认提供一个 `assistant` Agent 目录用于 `oryxos chat`。
- **Rationale**：为"一个目录=一个 Agent"打地基，同时保持本特性最小；完整的多 Agent/子指令/脚本属
  后续特性。
- **Alternatives**：本特性写死一个内置 profile（省事但偏离宪法 V，且后续要返工，不采）。

## 未决澄清

无 —— spec 的 `[NEEDS CLARIFICATION]` 已在 `/speckit-clarify` 全部解决（Provider 两家、天气源、
审计形式）。性能指标本特性不设（见 plan Technical Context）。
