# CLI Reference

The OryxOS CLI is built on **Picocli** and ships **12 commands** in the core stage. Commands that don't need an LLM (`init`, `profile list`) run via plain file operations for fast startup; those that do (`chat`, `serve`, `gateway`) boot the Spring context.

## Startup & status

| Command | Description |
|------|------|
| `oryxos init` | Initialize the `.oryxos/` workspace |
| `oryxos status` | Show config and runtime status |
| `oryxos chat [--profile <name>]` | Interactive chat; `--message "…"` sends one and exits |
| `oryxos serve` | Start the HTTP API (default 8080) |
| `oryxos gateway` | Start the multi-channel daemon |

## Profile management

| Command | Description |
|------|------|
| `oryxos profile list` | List all agents |
| `oryxos profile create <name>` | Create a new agent directory |
| `oryxos profile show <name>` | Show details |
| `oryxos profile delete <name>` | Delete |

## Queries

| Command | Description |
|------|------|
| `oryxos provider list` | List configured providers |
| `oryxos tool list` | List registered tools |
| `oryxos session list` | List session history |

## Three run modes

| Command | Mode | Notes |
|------|------|------|
| `oryxos chat` | Interactive | Primary local debug and daily use |
| `oryxos serve` | Web Service | Scheduling runs alongside `serve` |
| `oryxos gateway` | Daemon | Serves multiple channels at once |

All three share the same agent config and session store.

## Common examples

```bash
# Initialize and chat
oryxos init
export DEEPSEEK_API_KEY=sk-xxxxxx
oryxos chat --message "Hi, introduce yourself"

# Serve
oryxos serve

# Inspect registered tools and providers
oryxos tool list
oryxos provider list
```
