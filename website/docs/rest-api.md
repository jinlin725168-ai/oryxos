# REST API

The Web Service is the full external gateway for OryxOS; business systems reach every capability over REST. After `oryxos serve` (default port `8080`), the prefix is `/api/v1`.

> The core stage assumes an internal network with **no authentication**; API Key / JWT / RBAC / SSE streaming land in the extension stage.

## The 10 core endpoints

### Session management

| Method | Endpoint | Description |
|------|------|------|
| `POST` | `/sessions` | Create a session |
| `POST` | `/sessions/{id}/messages` | Send a message |
| `GET` | `/sessions/{id}` | Read history |
| `DELETE` | `/sessions/{id}` | Archive a session |

### Agent invocation & info

| Method | Endpoint | Description |
|------|------|------|
| `POST` | `/agents/{name}/invoke` | Stateless one-shot invoke |
| `GET` | `/profiles` | List available agents |
| `GET` | `/memory` | Read long-term memory |
| `GET` | `/tools` | List available tools |

### System

| Method | Endpoint | Description |
|------|------|------|
| `GET` | `/health` | Health check |
| `GET` | `/info` | Runtime info & provider status |

## Response envelope

Success and error share one JSON envelope:

```json
{
  "code": 0,
  "message": "success",
  "data": { },
  "timestamp": 1708838400000
}
```

Error codes: `400` bad params, `404` not found, `500` internal error, `503` provider failure.

## Integration patterns

- **Synchronous** (most common): call `invoke`, await return — good for short stateless tasks
- **Session-persistent**: create a session, then send multiple messages — good for continuous chat
- **Webhook-triggered**: alerting systems, CI/CD, scheduled tasks call an agent
- **Cross-language**: any language that can send HTTP requests works

## Examples

```bash
# Stateless invoke
curl -X POST http://localhost:8080/api/v1/agents/assistant/invoke \
  -H 'Content-Type: application/json' \
  -d '{"message":"summarize this quarterly report"}'

# Session-persistent
SID=$(curl -s -X POST http://localhost:8080/api/v1/sessions | jq -r .data.session_id)
curl -X POST http://localhost:8080/api/v1/sessions/$SID/messages \
  -d '{"message":"continue the previous topic"}'
```
