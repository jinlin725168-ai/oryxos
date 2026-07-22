# 命令行参考

OryxOS 命令行基于 **Picocli**，核心阶段提供 **12 个命令**。不需要 LLM 的命令（`init`、`profile list`）走纯文件操作快速启动；需要 LLM 的（`chat`、`serve`、`gateway`）才启动 Spring 上下文。

## 启动与状态

| 命令 | 说明 |
|------|------|
| `oryxos init` | 初始化 `.oryxos/` 工作区 |
| `oryxos status` | 查看配置和运行状态 |
| `oryxos chat [--profile <name>]` | 交互对话；`--message "…"` 发单条后退出 |
| `oryxos serve` | 启动 HTTP API 服务（默认 8080） |
| `oryxos gateway` | 启动多渠道守护进程 |

## Profile 管理

| 命令 | 说明 |
|------|------|
| `oryxos profile list` | 列出所有 Agent |
| `oryxos profile create <name>` | 创建新 Agent 目录 |
| `oryxos profile show <name>` | 查看详情 |
| `oryxos profile delete <name>` | 删除 |

## 查询

| 命令 | 说明 |
|------|------|
| `oryxos provider list` | 列出已配置的 Provider |
| `oryxos tool list` | 列出已注册的 Tool |
| `oryxos session list` | 列出会话历史 |

## 三种运行模式

| 命令 | 模式 | 说明 |
|------|------|------|
| `oryxos chat` | 交互对话 | 本地调试和日常使用主要方式 |
| `oryxos serve` | Web Service | 定时任务随 `serve` 一起常驻调度 |
| `oryxos gateway` | 守护进程 | 同时挂多个 Channel |

三种模式共享同一份 Agent 配置和 Session 存储。

## 常用示例

```bash
# 初始化并开聊
oryxos init
export DEEPSEEK_API_KEY=sk-xxxxxx
oryxos chat --message "你好，介绍一下你自己"

# 启动服务
oryxos serve

# 查看已注册的工具和 Provider
oryxos tool list
oryxos provider list
```
