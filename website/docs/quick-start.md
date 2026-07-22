# Quick Start

> OryxOS is under active development. The commands below describe the target experience.

## Prerequisites

- **JDK 21+** (required by Spring Boot 3.x)
- **Maven 3.9+**
- An API key for at least one LLM provider (DeepSeek, Qwen, Kimi, …)

## 1. Build

```bash
mvn clean package        # produces a single executable fat JAR
```

## 2. Initialize the workspace

```bash
oryxos init              # creates .oryxos/ in the current directory
```

Workspace layout:

```
.oryxos/
├── agents/            # each subdirectory = one agent
├── memory/MEMORY.md   # long-term memory
├── mcp_servers.yaml   # MCP server config
├── sessions/          # session history
├── logs/              # structured logs
├── AGENTS.md          # bootstrap: project-level behavior
├── SOUL.md            # bootstrap: default persona
├── USER.md            # bootstrap: user preferences
└── oryxos.db          # SQLite
```

## 3. Configure a key

Secrets are injected via environment variables; agent config uses `${ENV_VAR}` placeholders — **never hard-coded**.

```bash
export DEEPSEEK_API_KEY=sk-xxxxxx
```

## 4. Three ways to run

```bash
# Interactive chat (primary debug entry)
oryxos chat --message "check Beijing weather, what should I wear?"

# Serve the REST API (default 8080)
oryxos serve

# Multi-channel daemon
oryxos gateway
```

All three share the same agent config and session store — only the access layer differs.

## 5. Call over HTTP

```bash
curl -X POST http://localhost:8080/api/v1/agents/assistant/invoke \
  -H 'Content-Type: application/json' \
  -d '{"message":"summarize this quarterly report"}'
```

## Next

- [Defining an Agent](./agent) — ship an agent by writing one directory
- [Five Core Capabilities](./capabilities)
- [REST API](./rest-api)
