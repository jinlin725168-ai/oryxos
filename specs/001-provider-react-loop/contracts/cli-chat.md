# Contract: `oryxos chat` 命令

CLI 是本特性的对外契约。`oryxos chat` 启动交互式多轮对话。

## 用法

```
oryxos chat [--profile <name>] [--message "<单条消息>"]
```

| 参数 | 必填 | 默认 | 说明 |
|------|------|------|------|
| `--profile <name>` | 否 | `assistant` | 使用 `.oryxos/agents/<name>/AGENT.md` 定义的 Agent |
| `--message "<...>"` | 否 | 无 | 提供则发送单条消息、打印回答后退出；不提供则进入交互循环 |

## 行为契约

- **交互模式**（无 `--message`）：反复读取 stdin 一行 → 调 `AgentService.process(session, line)` →
  把最终回答写 stdout；输入 `/quit` 退出。同一进程内 `session` 持续累积历史（FR-001、FR-006）。
- **单条模式**（有 `--message`）：处理一条消息、打印最终回答、退出码 0。
- **工具调用对用户透明**：用户只看到最终回答；中间的工具调用不需要用户干预（FR-002/003/005）。
- **迭代上限**：单条消息内工具调用不超过 `maxIterations`（默认 10），到顶强制作答（FR-004）。
- **错误反馈**：
  - 工具失败 → 不中断对话，Agent 据回填的失败信息调整或如实告知（FR-008）。
  - LLM 调用失败 → 打印清晰错误信息（非堆栈），退出码非 0（FR-009；核心阶段无 failover）。
  - 凭证缺失/非法 → 启动即清晰报错，指明缺哪个环境变量（FR-012）。

## 前置条件

- 已 `oryxos init` 生成工作区，且存在目标 Agent 目录（`assistant` 默认提供）。
- 环境变量提供所用 Provider 的 key（如 `DEEPSEEK_API_KEY` 或 `ZHIPU_API_KEY`）。

## 退出码

| 码 | 含义 |
|----|------|
| 0 | 正常完成/退出 |
| 非 0 | LLM 调用失败、配置/凭证错误等 |
