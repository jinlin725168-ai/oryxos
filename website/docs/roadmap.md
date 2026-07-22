# Roadmap

OryxOS evolves in three stages — **the core stage is the foundation; enterprise governance is the endgame.**

## ✅ Core stage: single-node runtime kernel

Five core capabilities working, at parity with the base layer of open-source Agent OSes:

- Connect to any LLM (Provider abstraction)
- ReAct loop (self-implemented)
- Three-tier memory (session + long-term `MEMORY.md`)
- Plugin tools (9 built-ins + 3-tier extension)
- Web Service (10 core REST endpoints)
- CLI (12 commands), scheduling, session persistence, audit-to-DB, project homepage

**Release gate**: three daily end-to-end demos (daily weather, daily tech digest, daily GitHub digest) working.

## 🔜 Extension stage: production + governance

> This layer is what sets OryxOS apart from personal-scale Agent OSes.

- **Channels & models**: WeCom / Feishu / DingTalk / Slack channels; provider fallback & hedge racing; adaptive routing
- **Memory & capability**: automatic memory extraction; semantic vector search (LanceDB / pgvector / JVector); episodic memory; Memory Wiki
- **Tools & security**: Tool Policy; OryxOS as an MCP server; full sandbox (Docker / K8s / microVM)
- **Governance & ops**: web console; **SSO & multi-tenant RBAC** (SAML/OIDC, AD/Okta/Entra ID); full audit & SIEM export; clustered HA
- **Enterprise integration**: ready-made connectors for ERP / CRM / CMDB / monitoring

## 🌐 Community

No fixed timeline — open to community contribution as a long-term direction:

- Skills Marketplace (agentskills.io compatible)
- Multi-language SDKs (Java → Python → TypeScript → Go)
- Visual profile editor
- Kubernetes Operator, edge deploy (GraalVM Native Image)
- Voice channel, mobile console

## Beyond: from single-node to distributed agent collaboration

Once single-node is solid, move to multi-instance HA via "stateless instances + externalized state". Further out lies **distributed agent collaboration** — letting agents across departments, machines, even organizations discover each other across nodes, delegate reliably, and collaborate on cross-party work.

| Stage | Form | Focus |
|------|------|------|
| Stage 1 (now) | Single-node self-hosted | Complete runtime kernel |
| Stage 2 (mid) | Distributed foundation | Multi-instance + external state, HA |
| Stage 3 (far) | Distributed agent collaboration | Cross-node / cross-org discovery & delegation |
