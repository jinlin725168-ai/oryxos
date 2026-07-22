# 定义一个 Agent

在 OryxOS 里，**一个目录就是一个 Agent**（形态借鉴 Anthropic Agent Skills）。底座与 Agent 分成两层：

- **底座 = 系统基础能力**：Provider、ReAct、内置 Tool、Memory、Sandbox、定时、Web。所有 Agent 共享。
- **Agent = 一个目录** `.oryxos/agents/<name>/`：自足的业务 Agent，自带一切，**不再另写 Profile YAML**。

## 最简形态：光杆 AGENT.md

```markdown
---
name: daily-weather
description: 每天早上查天气并给穿搭建议
provider:
  name: deepseek
  model: deepseek-chat
tools: [http_get, notify]
notify_channels:
  - type: webhook
    url: ${WECOM_WEBHOOK_URL}
schedules:
  - cron: "0 0 8 * * ?"
    zone: Asia/Shanghai
    message: 查一下今天北京的天气，给出穿搭建议并推送到群里
---

你是一个贴心的生活助手。每天早上被触发时：
1. 用 http_get 查询北京今日天气
2. 根据温度和天气给出穿搭建议
3. 用 notify 把结果推送到企业微信群
```

放进 `.oryxos/agents/` 即可被 `AgentLoader` 加载：把 frontmatter 派生成一个 `Profile`，有 `schedules` 的交给 `AgentScheduler` 到点自动运行。

## 更丰富：子指令与脚本

一个 Agent 目录可以带更多资源，走**渐进式披露**——只有正文进 system prompt，其余按需读取：

```
.oryxos/agents/daily-tech/
├── AGENT.md            # 正文进 prompt
├── skills/format.md    # 子指令，LLM 用 read_file 按需读
├── scripts/fetch.py    # 脚本，LLM 用 shell 运行（产出进上下文、代码不进）
└── REFERENCE.md        # 参考资料
```

三种丰富度各对应一个验收 Demo：

| Demo | 形态 | 演示 |
|------|------|------|
| 每日天气 | 光杆 `AGENT.md` | 内置 HTTP Tool + 定时 |
| 每日科技日报 | `AGENT.md` + `skills/` | 子指令按需读 + MCP + Memory |
| 每日 GitHub 日报 | `AGENT.md` + `scripts/` | shell 跑脚本 + 信任边界 |

## 三档扩展工具

- **零代码**（主推）：`AGENT.md` + 复用社区 MCP，纯 markdown 上线新场景
- **轻代码**：任何语言写 MCP server，配在 `mcp_servers.yaml`
- **重代码**：Java `@Tool` Spring Bean，进程内直接调用

> **信任边界**：带脚本的 Agent，脚本经子进程自己发网络请求，绕过 `http_get` 的域名白名单。所以**装一个带脚本的 Agent = 信任这个 Agent 的作者**（与 Anthropic 一致）。容器 / 网络隔离留扩展阶段。
