# Five Core Capabilities

The OryxOS core stage prioritizes five capabilities. Together they cover a large share of real enterprise needs, and all belong to the runtime kernel that makes a single agent run well.

## 1. Connect to any LLM

A Provider abstraction wires in mainstream models (DeepSeek, Qwen, Kimi, Zhipu, Hunyuan, Doubao, Anthropic, OpenAI …). Agents never see which vendor is called; switching at runtime has no lock-in. The core stage builds directly on **Spring AI Alibaba**'s `ChatClient` — no reinvented wheels.

> **Key detail**: maintain an **explicit** provider-name → `ChatModel` map; never rely on type scanning to tell providers apart.

## 2. ReAct loop

ReAct (Reason + Act) is the agent's core mechanism: the LLM decides whether and which tool to call, observes the result, and decides the next step until it produces a final response. It force-stops at a max iteration count (default 10).

> **Key detail**: the loop is a few dozen lines of Java, **implemented ourselves** rather than using Spring AI's agent abstraction. We use only Spring AI's protocol conversion and schema generation and **disable its automatic tool execution**.

## 3. Three-tier memory

Agents remember preferences, projects, decisions, and history. The core stage ships two of three tiers:

| Tier | Description | Core stage |
|------|------|---------|
| Session | Full history of the current chat, auto-truncated when long | ✅ |
| Long-term | Stored in `MEMORY.md`, preserved across chats | ✅ (minimal) |
| Episodic | What was learned during each task | ⏳ Extension |

Agents read/write via two built-in tools: `save_memory` appends, `recall_memory` does keyword lookup. On startup the whole `MEMORY.md` is injected into the system prompt.

## 4. Plugin tools + built-in toolset

Agents act on the world via LLM function calling. Nine built-in tools: `read_file`, `write_file`, `list_dir`, `shell`, `http_get`, `http_post`, `save_memory`, `recall_memory`, `notify`. File / shell / HTTP tools all pass a sandbox allowlist check before executing.

Three extension tiers (lower is better):

| Tier | Effort | How | For |
|------|------|------|------|
| **① Zero-code** ⭐ | Lowest | Write `AGENT.md` + reuse MCP | Describe intent, LLM composes |
| **② Light-code** | Medium | MCP server in any language | Wire in your own systems |
| **③ Heavy-code** | Highest | Java `@Tool` bean | Deep integration, best perf |

See [Defining an Agent](./agent).

## 5. Web Service

A full REST API exposes every capability; business systems reach an agent over plain HTTP. This is what sets OryxOS apart from personal-scale projects, and the single channel for integration. See [REST API](./rest-api).

## Plus: scheduled tasks (third trigger source)

`AgentScheduler` fires calls on a cron schedule ("clock push"), reusing the same `AgentService` path as CLI and Web Service ("human push"). The `ReActLoop` never knows which entry a message came from.
