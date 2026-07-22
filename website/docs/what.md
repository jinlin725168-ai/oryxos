# What is OryxOS

**OryxOS is an Agent OS (agent operating system) for enterprises, built in Java.** It runs on your own K8s, servers, or bare metal as a unified foundation for business agents (ops, support, HR, sales, knowledge management), all sharing one stack of **channels, model routing, tool calling, memory, and sandboxed execution**.

Your data stays entirely on your own infrastructure — **never leaves, never locked to any cloud**. Teams ship a new agent by writing one directory plus tool config — **no agent backend code required**.

## Agent OS ≠ framework / orchestration platform

| | Product | Who uses it | Runs where |
|---|---|---|---|
| **Framework** (Spring AI, LangChain4j) | Code / SDK | Developers write code | You build the runtime |
| **Orchestration** (Dify, Coze) | Drag-and-drop workflow | Business / devs | On top of a runtime |
| **Agent OS (OryxOS)** | Configured long-running agents | Teams configure + devs write tools | **Your own machine / K8s** |

In one line: a framework gives you materials to build a house, an orchestration platform gives you a flow that runs on top of a runtime, and **OryxOS gives you the runtime itself** — a foundation where agents run as governed, auditable, long-lived services. All three compose; they do not compete.

## Why Java

Open-source Agent OSes are either `OpenClaw` (Node.js, consumer-leaning) or `Hermes` (Python, small-team-leaning) — **the Java ecosystem has nothing at the Agent OS layer**. Yet enterprise ERP, CRM, CMDB, SSO, and monitoring are overwhelmingly Java, and Spring AI Alibaba already solved the underlying LLM calls. The missing piece is exactly this "Agent OS" layer above. OryxOS fills it:

- **Spring Boot is the de-facto enterprise backend standard** — installing OryxOS is as natural as any Spring Boot app
- **Reuses the JVM ops toolchain** (Nacos, Sentinel, SkyWalking, Arthas, Prometheus)
- **Lowest integration cost with existing Java systems** — tools call your existing Spring beans directly
- **Regulated-industry self-hosting requirements make Java the deterministic choice**

## Delivered in two stages

**The core stage is the foundation; enterprise governance is the endgame.**

1. **Core stage** — build the Agent OS runtime kernel solidly in Java, at parity with the base layer of open-source Agent OSes
2. **Extension + community** — the differentiating governance layer (multi-tenancy, SSO, full audit, tool policy) lands on top over time

## Four design goals

- **Unified** — many agents share one foundation
- **Private** — data and deployment stay fully in your hands
- **Integrable** — standard Spring Boot structure, wires straight into existing systems
- **Observable** — Prometheus metrics, structured logs, health checks, web console

Next → [Quick Start](./quick-start) ｜ [Five Core Capabilities](./capabilities)
