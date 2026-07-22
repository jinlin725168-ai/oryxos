# 快速开始

> OryxOS 处于活跃开发阶段。下面的命令描述的是目标使用形态。

## 前置要求

- **JDK 21+**（Spring Boot 3.x 要求）
- **Maven 3.9+**
- 至少一个 LLM Provider 的 API Key（DeepSeek、通义、Kimi 等）

## 1. 构建

```bash
mvn clean package        # 生成单个可执行 fat JAR
```

## 2. 初始化工作区

```bash
oryxos init              # 在当前目录创建 .oryxos/
```

生成的工作区结构：

```
.oryxos/
├── agents/            # 每个子目录 = 一个 Agent
├── memory/MEMORY.md   # 长期记忆
├── mcp_servers.yaml   # MCP Server 配置
├── sessions/          # 会话历史
├── logs/              # 结构化日志
├── AGENTS.md          # Bootstrap：项目级行为说明
├── SOUL.md            # Bootstrap：默认人格
├── USER.md            # Bootstrap：用户偏好
└── oryxos.db          # SQLite
```

## 3. 配置密钥

敏感配置通过环境变量注入，Agent 配置里用 `${ENV_VAR}` 占位——**不明文写死**。

```bash
export DEEPSEEK_API_KEY=sk-xxxxxx
```

## 4. 三种运行方式

```bash
# 交互对话（主要调试入口）
oryxos chat --message "查一下北京天气，告诉我今天穿什么"

# 启动 REST API 服务（默认 8080）
oryxos serve

# 多渠道守护进程
oryxos gateway
```

三种模式共享同一份 Agent 配置和 Session 存储，差异只是接入层。

## 5. 通过 HTTP 调用

```bash
curl -X POST http://localhost:8080/api/v1/agents/assistant/invoke \
  -H 'Content-Type: application/json' \
  -d '{"message":"总结这份季度报告"}'
```

## 下一步

- [定义一个 Agent](./agent) —— 写一个目录就上线一个 Agent
- [五大核心能力](./capabilities)
- [REST API](./rest-api)
