# Tasks: Provider 抽象 + ReAct 工具调用对话

**Input**: Design documents from `specs/001-provider-react-loop/`
**Prerequisites**: plan.md、spec.md、research.md、data-model.md、contracts/
**Tests**: 仅按 plan 的 Testing 明确列出的关键单元测（ReActLoop 终止、Sandbox 白名单、Provider 映射）。
**Organization**: 按用户故事分阶段，每个故事可独立实现与验收。

> **前提**：9 模块 Maven 骨架与 `oryxos-boot` 已可 `mvn clean package` + `java -jar`；以下任务多为
> **在既有 stub 文件中填实现**。文件路径为仓库真实路径。

## Format: `[ID] [P?] [Story] Description`
- **[P]**：可并行（不同文件、无未完成依赖）
- **[Story]**：US1/US2/US3（对应 spec 的三个用户故事）

---

## Phase 1: Setup（共享基础）

- [ ] T001 [P] 在 `oryxos-provider/pom.xml` 激活并钉版本 Spring AI Alibaba（OpenAI 兼容 ChatModel）依赖，确认 `mvn -o dependency:resolve` 通过
- [ ] T002 [P] 新建 `oryxos-boot/src/main/resources/application.yaml`，配置 `oryxos.providers`（deepseek、zhipu 的 base-url/model/`${DEEPSEEK_API_KEY}`/`${ZHIPU_API_KEY}`），见 `contracts/provider-service.md`
- [ ] T003 [P] 在 `application.yaml` 配置 `oryxos.sandbox.http.allowed-domains` 放行 `api.open-meteo.com`、`geocoding-api.open-meteo.com`

---

## Phase 2: Foundational（阻塞性前置 —— 所有故事的公共地基）

**⚠️ CRITICAL**: 本阶段完成前，任何用户故事不能开工。

- [ ] T004 [P] 扩展会话模型：在 `oryxos-core/src/main/java/com/oryxos/core/session/` 增加 `Message`（role: system/user/assistant/tool、`toolCalls`、`toolCallId`、`name`）并完善 `Session`（见 data-model.md）。Foundational 用最简内存 `Session`（单轮足够）；多轮保持与截断的 `SessionManager` 由 US2 的 T024 引入
- [ ] T005 [P] 定义 `ProviderConfig`（`oryxos-provider/.../provider/ProviderConfig.java`）+ `ConfigLoader`（`oryxos-cli/.../cli/ConfigLoader.java`）从环境变量解析 `${...}`，缺失时清晰报错（FR-012）
- [ ] T006 实现 `ProviderService` 显式映射：启动构建 `Map<String,ChatModel>`（key=`deepseek`/`zhipu`），`chat(providerName, prompt, tools)` 直调 `ChatModel.call`，**禁用自动 tool 执行** —— `oryxos-provider/.../provider/ProviderService.java`（依赖 T005；宪法 III/IV）
- [ ] T007 [P] 实现 `FunctionCallingAdapter`：`OryxTool` ↔ Spring AI 工具声明/协议格式转换（只声明、不自动执行）—— `oryxos-provider/.../provider/FunctionCallingAdapter.java`
- [ ] T008 [P] 结构化审计日志：`LlmCall` / `ToolInvocation` 事件（Logback JSON，字段对齐未来表、脱敏）—— `oryxos-core/.../core/audit/`（FR-011）
- [ ] T009 [P] 实现 `AgentLoader.deriveProfile` + `ContextLoader`：读 `.oryxos/agents/<name>/AGENT.md` frontmatter 派生 `Profile`、正文注入 system prompt —— `oryxos-core/.../core/profile/`（宪法 V）
- [ ] T010 [P] 实现 `PromptBuilder`：拼装 system(AGENT.md 正文)+对话历史(按 `maxHistoryTurns` 截断)+工具声明 —— `oryxos-core/.../core/engine/PromptBuilder.java`
- [ ] T011 实现 `ReActLoop` 基线（Reason-only）：调 `ProviderService`，无 `toolCalls` 直接返回，含 `maxIterations` 骨架（Act 分支在 US1 接入）—— `oryxos-core/.../core/engine/ReActLoop.java`（依赖 T006, T010）
- [ ] T012 实现 `AgentService.process` 编排：加载 Profile → 跑 `ReActLoop` → 返回，并写 LlmCall 审计 —— `oryxos-core/.../core/engine/AgentService.java`（依赖 T011, T008）
- [ ] T013 CLI 装配：实现 `oryxos init`（生成 `.oryxos/` + 默认 `assistant` Agent 目录及 `AGENT.md`，frontmatter `tools: [http_get]`）与 `oryxos chat --message`（单条模式先通）接 `CliChannel` → `AgentService` —— `oryxos-cli/.../cli/OryxOsCli.java`、`oryxos-channel-cli/.../channel/cli/CliChannel.java`、`oryxos-boot`（依赖 T012）

**Checkpoint**：可运行 `oryxos chat --message "用一句话解释 ReAct"` 得到直接作答（无工具）。

---

## Phase 3: User Story 1 - 一次对话内自动调用工具完成任务 (P1) 🎯 MVP

**Goal**：单条消息内 LLM 自主决定调工具 → OryxOS 执行 → 回填 → 继续推理 → 最终作答。

**Independent Test**：`oryxos chat --message "查一下北京天气并告诉我今天穿什么"` 无人工干预自动完成工具调用并给出结合天气的回答。

### Tests（plan 指定的关键单元测）
- [ ] T014 [P] [US1] 单元测 `ReActLoop`：有/无 `toolCalls` 分支、达到 `maxIterations` 强制终止 —— `oryxos-core/src/test/java/com/oryxos/core/engine/ReActLoopTest.java`
- [ ] T015 [P] [US1] 单元测 `WhitelistSandbox`：域名白名单放行/拒绝、`../` 无关的 host 通配匹配 —— `oryxos-tool/src/test/java/com/oryxos/tool/sandbox/WhitelistSandboxTest.java`

### Implementation
- [ ] T016 [US1] 实现 `WhitelistSandbox.enforce` 的 `checkHttpUrl`（解析 host、通配匹配、放行 open-meteo，越界抛 `SandboxViolationException`；绑定 `application.yaml` 的 `oryxos.sandbox.http.allowed-domains` 配置）—— `oryxos-tool/.../tool/sandbox/WhitelistSandbox.java`
- [ ] T017 [US1] 实现 `HttpTools.http_get`（执行前 `Sandbox.enforce(HTTP_REQUEST,url)`）—— `oryxos-tool/.../tool/builtin/HttpTools.java`（依赖 T016）
- [ ] T018 [US1] 配置"天气"能力（**零代码**，宪法 V / 工具三档取最低）：在默认 Agent 的 `AGENT.md` 正文写明"查天气用 `http_get` 两步查 open-meteo（geocoding→forecast）再综合作答"，**不写 Java 工具类** —— `.oryxos/agents/assistant/AGENT.md`（依赖 T013 先建目录/frontmatter；见 `contracts/weather-skill.md`）
- [ ] T019 [US1] 实现 `ToolExecutor`：从 `ToolRegistry` 取 Tool → `Sandbox.enforce` → `execute` → 包装 `ToolResult` → 写 ToolInvocation 审计 —— `oryxos-core/.../core/engine/ToolExecutor.java`（依赖 T016, T008）
- [ ] T020 [US1] `ToolRegistry` 注册内置 Tool（本特性即 `http_get`）并按 Profile `tools` 过滤可用子集 —— `oryxos-tool/.../tool/ToolRegistry.java`（依赖 T017）
- [ ] T021 [US1] `ReActLoop` 接入 Act 分支：解析 `assistant.toolCalls` → `ToolExecutor` 执行 → 追加 tool 消息 → 继续循环，直到无 `toolCalls` 或到 `maxIterations` —— `oryxos-core/.../core/engine/ReActLoop.java`（依赖 T011, T019）
- [ ] T022 [US1] 错误路径：工具失败回填不中断对话（FR-008）、LLM 调用失败清晰报错（FR-009）、越界工具被拒并回填 —— `ReActLoop`/`ToolExecutor`/`CliChannel`

**Checkpoint**：US1 完整，可独立演示"查天气穿衣"（Demo 一）。

---

## Phase 4: User Story 2 - 多轮对话与上下文延续 (P2)

**Goal**：交互式多轮，Agent 记得先前轮次。

**Independent Test**：交互问"北京天气怎么样"，再追问"那适合穿什么"（不重复城市），Agent 正确沿用上下文。

### Implementation
- [ ] T023 [US2] `CliChannel` 交互循环：反复读 stdin、同一 `Session` 累积历史、`/quit` 退出 —— `oryxos-channel-cli/.../channel/cli/CliChannel.java`（依赖 T013）
- [ ] T024 [US2] `SessionManager` 内存版历史保持 + 超 `maxHistoryTurns` 截断（保留近期、丢弃最早）—— `oryxos-core/.../core/session/SessionManager.java`（依赖 T004）

**Checkpoint**：US1 与 US2 均独立可用。

---

## Phase 5: User Story 3 - 模型无关（换 Provider 不改变行为） (P3)

**Goal**：把 Agent 的 `provider.name` 在 deepseek/zhipu 间切换，对话与工具调用行为不变。

**Independent Test**：同一段查天气对话脚本在 deepseek、zhipu 下各跑一次，均完成工具调用并给出合理回答。

### Tests
- [ ] T025 [P] [US3] 单元测 `ProviderService`：按 name 选对 `ChatModel`（deepseek≠zhipu，映射无歧义）—— `oryxos-provider/src/test/java/com/oryxos/provider/ProviderServiceTest.java`

### Implementation
- [ ] T026 [US3] 校验 zhipu Provider 配置就绪，可通过 `AGENT.md` 的 `provider.name` 切换且无需改动任何对话用法 —— `application.yaml` / `ProviderService`（依赖 T006）

**Checkpoint**：三个故事均独立可用。

---

## Phase 6: Polish & Cross-Cutting

- [ ] T027 [P] 复核审计日志：不含明文凭证、字段与未来 `llm_calls`/`tool_invocations` 表对齐（FR-011/012）
- [ ] T028 运行 `quickstart.md` 全部 5 个场景做端到端验收（双 Provider + open-meteo）
- [ ] T029 [P] 移除本特性相关 `// TODO(US-1)/(US-2)` 占位，确认 `mvn clean package` 与 `java -jar oryxos-boot/target/oryxos.jar` 正常
- [ ] T030 [P] 更新文档：把 `oryxos chat` 工具调用用法补进 `website/zh/docs/quick-start.md`（可选）

---

## Dependencies & Execution Order

- **Setup(Phase 1)**：无依赖，可立即并行。
- **Foundational(Phase 2)**：依赖 Setup；**阻塞所有用户故事**。内部关键链：T005→T006→T011→T012→T013；T004/T007/T008/T009/T010 可并行。
- **US1(Phase 3)**：依赖 Foundational。内部：T016→T017；T013→T018（同一 `AGENT.md`：先建后写正文，不并行）；T016+T008→T019；T017→T020；T011+T019→T021→T022。
- **US2(Phase 4)**：依赖 Foundational（+ 复用 US1 的 Session）；T023、T024 顺序解耦但同属会话层。
- **US3(Phase 5)**：依赖 Foundational（Provider 映射 T006 已就绪），几乎只是配置 + 验证。
- **Polish(Phase 6)**：依赖所需故事完成。

### 用户故事独立性
- US1 是 MVP，独立可测（Demo 一）。US2 在 US1 之上加多轮，独立可测。US3 主要是配置切换验证，独立可测。

---

## Parallel Example: Foundational

```text
# T004/T007/T008/T009/T010 可并行（不同文件、互不依赖）：
Task: T004 扩展 Message/Session 模型
Task: T007 FunctionCallingAdapter
Task: T008 结构化审计日志
Task: T009 AgentLoader.deriveProfile + ContextLoader
Task: T010 PromptBuilder
```

## Parallel Example: User Story 1

```text
# 单元测并行（不同文件）：
Task: T014 ReActLoopTest
Task: T015 WhitelistSandboxTest
# 注：T018（写 AGENT.md 正文）依赖 T013、与其同文件，不并行
```

---

## Implementation Strategy

### MVP First（仅 US1）
1. Phase 1 Setup → 2. Phase 2 Foundational（阻塞，务必先完成）→ 3. Phase 3 US1 → 4. **停下验收**：跑通"查天气穿衣" → 5. 演示 Demo 一。

### Incremental Delivery
Setup+Foundational → US1（MVP，git commit）→ US2（多轮，commit）→ US3（模型无关，commit）→ Polish。每个故事结束后跑 `/speckit-analyze` 一致性检查（宪法工作流）。

---

## Notes
- [P] = 不同文件、无未完成依赖，可并行。
- 每个 checkpoint 可停下独立验收该故事。
- 每完成一个任务或一组逻辑 commit 一次。
- 守住宪法：ReActLoop 自实现（II）、`ChatModel.call` 不自动执行（III）、Provider 显式映射（IV）、AGENT.md 不进 ToolRegistry（V）、白名单+环境变量+审计日志（VI）、同步+虚拟线程（VII）。

---

## Phase 7: Convergence

> 由 `/speckit-converge` 追加（append-only）。评估时代码处于骨架阶段（尚未 `/speckit-implement`），
> spec/plan/宪法 的其余可构建义务均已由 T001–T030 覆盖，故不重复追加；此处仅列**未被任何现有任务
> 覆盖**的遗漏项。

- [ ] T031 启用 Java 21 虚拟线程：在 `oryxos-boot/src/main/resources/application.yaml` 设 `spring.threads.virtual.enabled: true`，并确认 `chat`/`serve` 走同步阻塞 + 虚拟线程路径（不引入 Reactor/WebFlux/CompletableFuture）per Constitution VII / plan Constraints (missing)
