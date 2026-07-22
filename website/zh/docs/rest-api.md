# REST API

Web Service 是 OryxOS 的对外完整门面，业务系统通过 REST API 接入所有能力。`oryxos serve` 启动后（默认端口 `8080`），前缀 `/api/v1`。

> 核心阶段假设内网**无认证**；API Key / JWT / RBAC / SSE 流式响应随扩展阶段补齐。

## 核心 10 个端点

### 会话管理

| 方法 | 端点 | 说明 |
|------|------|------|
| `POST` | `/sessions` | 创建会话 |
| `POST` | `/sessions/{id}/messages` | 发消息 |
| `GET` | `/sessions/{id}` | 查历史 |
| `DELETE` | `/sessions/{id}` | 归档会话 |

### Agent 调用与信息

| 方法 | 端点 | 说明 |
|------|------|------|
| `POST` | `/agents/{name}/invoke` | 无状态调用一次 Agent |
| `GET` | `/profiles` | 列出可用 Agent |
| `GET` | `/memory` | 查长期记忆 |
| `GET` | `/tools` | 列出可用工具 |

### 系统状态

| 方法 | 端点 | 说明 |
|------|------|------|
| `GET` | `/health` | 健康检查 |
| `GET` | `/info` | 运行信息与 Provider 状态 |

## 响应信封

成功与错误共用一个 JSON 信封：

```json
{
  "code": 0,
  "message": "success",
  "data": { },
  "timestamp": 1708838400000
}
```

错误码：`400` 参数错误、`404` 资源不存在、`500` 内部错误、`503` Provider 故障。

## 集成场景

- **同步调用**（最常用）：调 `invoke` 等返回，适合 stateless 短任务
- **会话保持**：先创建 Session 再多次发消息，适合连续对话
- **Webhook 触发**：告警系统、CI/CD、定时任务调 Agent
- **跨语言集成**：任何能发 HTTP 请求的语言都能接

## 调用示例

```bash
# 无状态调用
curl -X POST http://localhost:8080/api/v1/agents/assistant/invoke \
  -H 'Content-Type: application/json' \
  -d '{"message":"总结这份季度报告"}'

# 会话保持
SID=$(curl -s -X POST http://localhost:8080/api/v1/sessions | jq -r .data.session_id)
curl -X POST http://localhost:8080/api/v1/sessions/$SID/messages \
  -d '{"message":"继续上一个话题"}'
```
