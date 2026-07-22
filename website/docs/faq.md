# FAQ

## How does OryxOS differ from OpenClaw and Hermes?

Same category, different positioning. All three are Agent OSes: OpenClaw (Node.js) leans consumer, Hermes (Python) leans small-team, and **OryxOS targets regulated enterprises directly** — Java-native, self-hosted, auditable from day one. They interoperate via `SKILL.md`; vetted community skills can, in principle, be imported into OryxOS after enterprise review. The ecosystems are complementary, not competitive.

## Does OryxOS compete with Dify or Coze?

No — they are complementary. Dify orchestrates a "flow" (drag-and-drop workflow); OryxOS hosts a "long-running agent". If a use case needs a complex workflow, run Dify on top of OryxOS (Dify as the app layer calling the OryxOS API).

## What's the relationship with Spring AI?

Reuse. The OryxOS provider abstraction is built directly on Spring AI Alibaba's LLM connectors — no reinvented wheels. But OryxOS uses **only** Spring AI's protocol conversion and schema generation and **disables its automatic tool execution** — tool scheduling is fully controlled by the self-implemented `ReActLoop` + `ToolExecutor`.

## Why no vector database in the core stage?

LanceDB's Java embedded support is still in development; other vector stores (Qdrant, Milvus) need external processes, and pgvector needs external PostgreSQL — none fit the "single-binary deploy" positioning. The core stage ships the shortest path with SQLite + `MEMORY.md` + keyword search; semantic search lands in the extension stage, and the interface already reserves room to upgrade.

## Does any data leave my infrastructure?

No. OryxOS runs on your own infrastructure and **collects no enterprise data itself**. All sessions, memory, and audit data stay local. LLM calls go through providers you configure (including local inference like Ollama / vLLM, keeping data fully in-house).

## What deployment options are supported?

Bare metal, VMs, Docker, and Kubernetes. The core stage is a single executable fat JAR (`java -jar`); the extension stage compiles to a native binary via GraalVM Native Image, cutting startup below 100 ms.

## What's the security boundary in the core stage?

The core stage uses **app-level allowlists** for basic isolation: file path allowlist, shell command allowlist, HTTP domain allowlist, plus execution timeouts. This is a "deterrent-grade" line of defense against models doing something dumb — it **does not stop deliberate bypass**, so the core stage is not recommended for running fully untrusted code in production. Full container / microVM sandboxing, authentication, and SSO land in the extension stage.

## What environments are supported?

- **JDK**: 21 and above
- **OS**: mainstream Linux (Ubuntu 22.04+, CentOS 8+, Debian 11+, Alibaba Cloud Linux 3, Rocky Linux)
- **LLM protocol**: the OpenAI-compatible protocol is the de-facto standard — any provider implementing it connects directly
