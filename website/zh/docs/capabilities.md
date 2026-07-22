# 五大核心能力

OryxOS 核心阶段优先做五个核心能力，基于它们可以扩展出企业里大量真实需求。这五个都属于"让单个 Agent 跑得好"的运行时内核层。

## 能力一：对接 LLM

通过 Provider 抽象层对接主流大模型（DeepSeek、通义、Kimi、智谱、混元、豆包、Anthropic、OpenAI 等），Agent 不感知具体调的是哪家，运行时切换无 lock-in。核心阶段直接基于 **Spring AI Alibaba** 的 `ChatClient` 实现，不重复造轮子。

> **关键实现**：维护 provider-name → `ChatModel` 的**显式映射**，不靠类型扫描区分多个 Provider。

## 能力二：ReAct 循环

ReAct（Reason + Act）是 Agent 的核心工作机制：接到任务后，LLM 思考要不要调工具、调哪个，调用之后看结果，再决定下一步，直到给出最终响应。达到最大迭代次数（默认 10）强制结束。

> **关键实现**：核心循环约数十行 Java，**自己实现**而不依赖 Spring AI 的 Agent 抽象；只用 Spring AI 的协议转换和 schema 生成，**禁用其自动 tool 执行**。

## 能力三：Memory 三层记忆

Agent 记得住用户偏好、项目、决策、对话历史。三层设计，核心阶段先实现会话 + 长期两层：

| 层次 | 说明 | 核心阶段 |
|------|------|---------|
| 会话记忆 | 当前对话完整历史，过长自动截断 | ✅ |
| 长期记忆 | 存在 `MEMORY.md`，跨对话保留 | ✅（极简版）|
| 情景记忆 | 每个任务学到的东西 | ⏳ 扩展阶段 |

Agent 通过两个内置 Tool 主动读写：`save_memory` 追加、`recall_memory` 关键词检索。启动时 `MEMORY.md` 整个注入 system prompt。

## 能力四：Plugin 自定义工具 + 内置工具集

Agent 通过 LLM Function Calling 调用工具实际操作系统。内置 9 个 Tool：`read_file`、`write_file`、`list_dir`、`shell`、`http_get`、`http_post`、`save_memory`、`recall_memory`、`notify`。文件 / Shell / HTTP 类执行前统一走 Sandbox 白名单校验。

业务方三档扩展（能低就不高）：

| 方式 | 门槛 | 做法 | 场景 |
|------|------|------|------|
| **① 零代码** ⭐ | 最低 | 写 `AGENT.md` + 复用 MCP | 描述意图，LLM 自己组合 |
| **② 轻代码** | 中等 | 任何语言写 MCP server | 接入企业自有系统 |
| **③ 重代码** | 最高 | Java `@Tool` Bean | 深度集成，性能最好 |

详见 [定义一个 Agent](./agent)。

## 能力五：Web Service

通过完整 REST API 把所有能力对外暴露，业务系统用 HTTP 调一下就能用上 Agent。这是 OryxOS 区别于偏个人定位项目的关键能力，也是企业集成 OryxOS 的唯一通道。详见 [REST API](./rest-api)。

## 加一个：定时任务（第三触发源）

`AgentScheduler` 按 cron 到点自动发起调用（"钟推"），跟 CLI、Web Service（"人推"）复用同一条 `AgentService` 链路，`ReActLoop` 不感知消息从哪个入口来。
