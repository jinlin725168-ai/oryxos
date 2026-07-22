# Architecture

OryxOS is a **Spring Boot 3.x** monolith on **JDK 21**, using **Spring AI Alibaba** for LLM calls and a self-implemented **ReAct loop** as the agent core. The whole thing is one executable JAR — single-binary deploy.

![OryxOS architecture](/architecture.svg)

## Layered view

Five layers top to bottom, every edge flowing down:

1. **Access layer** (three trigger sources): CLI Channel, Web Service, `AgentScheduler` cron
2. **Unified entry**: `AgentService` — all three sources converge into one path; the engine never knows the source
3. **Engine layer**: `ReActLoop`, `PromptBuilder`, `ToolExecutor` — the agent's brain
4. **Capability layer**: Provider, Memory, Tool — LLM calls, context, execution
5. **Foundation layer**: sessions and audit in SQLite; agent dirs / bootstrap / memory on the filesystem

> In one line: Provider, Memory, and Tool feed the ReAct engine; the engine's output is served through three entries — CLI, Web Service, and scheduling.

## Seven key technical decisions

| # | Decision | Choice |
|---|------|------|
| 1 | ReAct loop | Self-implemented, not Spring AI's agent abstraction |
| 2 | Spring AI boundary | Provider abstraction + protocol conversion + schema only; **auto tool execution disabled** |
| 3 | Execution model | Synchronous blocking + JDK 21 virtual threads |
| 4 | Tool registration | `@Tool` annotation + `OryxTool` abstraction unifying built-in and MCP tools |
| 5 | HTTP layer | Spring MVC + virtual threads |
| 6 | Sandbox | Interface-first; core stage `WhitelistSandbox` (app-level allowlist) |
| 7 | Persistence | SQLite + Spring Data JPA + `MEMORY.md`; audit tables written from day one |

## Nine Maven modules

| Module | Responsibility |
|------|------|
| `oryxos-core` | Core abstractions & engine: `ReActLoop`, `PromptBuilder`, `ToolExecutor`, `AgentService`, `AgentScheduler` |
| `oryxos-provider` | LLM: `ProviderService`, function-calling adapter |
| `oryxos-memory` | Memory: `MemoryService`, `LongTermMemoryStore`, `MemoryTools` |
| `oryxos-tool` | Tools: built-ins, MCP client, `ToolRegistry`, `Sandbox` |
| `oryxos-channel-cli` | CLI Channel |
| `oryxos-web` | Web Service: 6 controllers, exception handler, OpenAPI |
| `oryxos-storage` | SQLite persistence |
| `oryxos-cli` | Picocli entry, 12 subcommands, config loading |
| `oryxos-boot` | Spring Boot startup & dependency aggregation |

Modules are decoupled by interfaces; new channels or tools are added as new modules without touching the core engine.

## Persistence

Core stage uses five SQLite tables: `sessions`, `tool_invocations`, `llm_calls`, `scheduled_tasks`, `task_executions`. Long-term memory is the `MEMORY.md` file, not a table.

> **Audit from day one**: `tool_invocations` and `llm_calls` are written to the database in the core stage (not just logs), standing up the data foundation for the auditability differentiator from day one.
