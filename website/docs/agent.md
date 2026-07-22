# Defining an Agent

In OryxOS, **one directory is one agent** (form borrowed from Anthropic Agent Skills). Foundation and agent are two layers:

- **Foundation = system capabilities**: Provider, ReAct, built-in tools, Memory, Sandbox, scheduling, Web. Shared by all agents.
- **Agent = a directory** `.oryxos/agents/<name>/`: a self-contained business agent that carries everything — **no separate Profile YAML**.

## Minimal form: a bare AGENT.md

```markdown
---
name: daily-weather
description: Fetch weather each morning and suggest what to wear
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
    message: Check today's Beijing weather, suggest an outfit, push to the group
---

You are a thoughtful daily assistant. When triggered each morning:
1. Use http_get to fetch today's Beijing weather
2. Suggest an outfit based on temperature and conditions
3. Use notify to push the result to the team IM group
```

Drop it into `.oryxos/agents/` and `AgentLoader` loads it: the frontmatter derives a `Profile`, and any `schedules` are handed to `AgentScheduler` to run on time.

## Richer: sub-instructions and scripts

An agent directory can carry more resources via **progressive disclosure** — only the body enters the system prompt; the rest is read on demand:

```
.oryxos/agents/daily-tech/
├── AGENT.md            # body enters the prompt
├── skills/format.md    # sub-instruction, read via read_file when needed
├── scripts/fetch.py    # script, run via shell (output enters context, code does not)
└── REFERENCE.md        # reference material
```

Three richness levels each map to an acceptance demo:

| Demo | Form | Shows |
|------|------|------|
| Daily weather | bare `AGENT.md` | built-in HTTP tool + scheduling |
| Daily tech digest | `AGENT.md` + `skills/` | on-demand sub-instruction + MCP + Memory |
| Daily GitHub digest | `AGENT.md` + `scripts/` | shell-run script + trust boundary |

## Three extension tiers

- **Zero-code** (recommended): `AGENT.md` + reuse community MCP — pure markdown ships a new scenario
- **Light-code**: an MCP server in any language, declared in `mcp_servers.yaml`
- **Heavy-code**: a Java `@Tool` Spring bean, called in-process

> **Trust boundary**: for an agent with scripts, the script makes its own network calls from a subprocess, bypassing `http_get`'s domain allowlist. So **installing an agent with scripts = trusting that agent's author** (same as Anthropic). Container / network isolation lands in the extension stage.
