<script setup>
import { computed } from 'vue'
import { useData } from 'vitepress'

const { lang } = useData()
const isZh = computed(() => lang.value === 'zh-CN')
const t = (zh, en) => (isZh.value ? zh : en)
const docBase = computed(() => (isZh.value ? '/zh/docs' : '/docs'))

const capabilities = computed(() => [
  {
    icon: '🔌',
    title: t('对接 LLM', 'Connect to any LLM'),
    subtitle: t('Provider 抽象 · 运行时切换无 lock-in', 'Provider abstraction · switch at runtime, no lock-in'),
    code: `# Configure a Provider; Profiles reference it by name
provider:
  name: deepseek        # deepseek / qwen / kimi / ...
  model: deepseek-chat
  api_key: \${DEEPSEEK_API_KEY}

# Agents never see which vendor is called
oryxos provider list`,
  },
  {
    icon: '🧠',
    title: t('ReAct 循环', 'ReAct loop'),
    subtitle: t('Agent 大脑 · 思考 + 工具执行', "The agent's brain · reason + act"),
    code: `# reason -> act -> observe -> repeat
oryxos chat --message "check Beijing weather, what to wear?"

# The agent decides on its own:
#   call http_get -> read JSON
#   -> write advice -> reply. No flow to hand-code.`,
  },
  {
    icon: '💾',
    title: t('Memory 三层记忆', 'Three-tier memory'),
    subtitle: t('会话 + 长期 (MEMORY.md) 跨对话保留', 'Session + long-term (MEMORY.md) across chats'),
    code: `# The agent writes to long-term memory itself
save_memory("User is on Spring Boot, deploys to K8s")

# and recalls it by keyword next time
recall_memory("database")
# MEMORY.md is injected into the system prompt on start`,
  },
  {
    icon: '🛠️',
    title: t('Plugin 工具', 'Plugin tools'),
    subtitle: t('内置 9 个 + 三档扩展，主推零代码', '9 built-ins + 3-tier extension, zero-code first'),
    code: `# One directory = one agent, zero Java
.oryxos/agents/daily-tech/
  AGENT.md            # frontmatter + task prompt
  skills/format.md    # sub-instruction, read on demand

# Reuse community MCP servers — no tool code
mcp_servers.yaml: [github-mcp, slack-mcp]`,
  },
  {
    icon: '🌐',
    title: t('Web Service', 'Web Service'),
    subtitle: t('REST API 是对外集成的唯一门面', 'REST API — the one gateway for integration'),
    code: `# Any business system reaches an agent over HTTP
curl -X POST \\
  http://localhost:8080/api/v1/agents/assistant/invoke \\
  -H 'Content-Type: application/json' \\
  -d '{"message":"summarize this quarterly report"}'`,
  },
])

const scenarios = computed(() => [
  {
    num: '01',
    title: t('运维助手', 'Ops assistant'),
    desc: t('凌晨告警经 webhook 进来，Agent 拉日志、跟历史故障交叉引用、自动重启并在 IM 群汇报"已自愈"。', 'Alerts arrive via webhook; the agent pulls logs, cross-references past incidents, self-heals, and reports "resolved" to the IM group.'),
  },
  {
    num: '02',
    title: t('知识管理助手', 'Knowledge assistant'),
    desc: t('员工在飞书里问历史合同条款，Agent 检索 Memory 拉出案例、综合法规给出草稿，并标注引用来源可追溯。', 'Staff ask about past contract clauses; the agent retrieves cases from memory, drafts an answer, and cites traceable sources.'),
  },
  {
    num: '03',
    title: t('销售助手', 'Sales assistant'),
    desc: t('拜访前问"我该知道什么"，Agent 调 CRM connector 拉交易记录、调企查查 MCP 查工商信息，综合输出客户简报。', 'Before a visit, the agent pulls CRM history and external company data via MCP, then outputs a client brief.'),
  },
  {
    num: '04',
    title: t('研发助手', 'Dev assistant'),
    desc: t('接 GitHub / Jira / CI，读代码改代码、记住项目惯例，通过 IDE 插件或 Web Service 接入研发流程。', 'Wired to GitHub / Jira / CI, it reads and edits code, remembers project conventions, and plugs into the dev workflow.'),
  },
  {
    num: '05',
    title: t('每日天气（定时）', 'Daily weather (scheduled)'),
    desc: t('AgentScheduler 到点自动跑：查天气、生成穿搭建议、推送到 IM 群。光杆 AGENT.md，一行 cron 即可。', 'AgentScheduler fires on cron: fetch weather, write advice, push to IM. A bare AGENT.md and one cron line.'),
  },
  {
    num: '06',
    title: t('每日科技日报（零代码）', 'Daily tech digest (zero-code)'),
    desc: t('业务方只写 AGENT.md + 子指令，LLM 自己调新闻 MCP、按记忆偏好组稿、调 notify 推送，不写一行 Java。', 'Just an AGENT.md plus a sub-instruction — the LLM calls a news MCP, writes per remembered preferences, and pushes. No Java.'),
  },
  {
    num: '07',
    title: t('每日 GitHub 日报（脚本）', 'Daily GitHub digest (script)'),
    desc: t('Agent 目录捆绑一个 Python 脚本，用 shell 跑脚本拿确定性数据，再组织三段日报并推送。', 'The agent directory bundles a Python script; it runs it via shell for deterministic data, then composes and pushes a 3-part digest.'),
  },
  {
    num: '08',
    title: t('数据分析', 'Data analysis'),
    desc: t('自然语言生成 SQL、执行查询、出图，记住业务表结构，通过 Web Service 嵌入 BI 工具。', 'Generate SQL from natural language, run queries, chart results, remember schemas, and embed into BI via the Web Service.'),
  },
])

const tiers = computed(() => [
  {
    featured: true,
    icon: '📄',
    title: t('零代码 · AGENT.md + MCP', 'Zero-code · AGENT.md + MCP'),
    desc: t('写一个目录就定义一个 Agent，正文描述任务，LLM 自己组合调用社区现成的 MCP 工具。整个过程不写一行代码。', 'Define an agent with one directory — the prompt describes the task and the LLM composes community MCP tools. Not a single line of code.'),
    badges: ['AGENT.md', 'MCP', 'SKILL.md'],
    note: t('★ 主推方式', '★ Recommended'),
  },
  {
    icon: '📦',
    title: t('轻代码 · 自写 MCP server', 'Light-code · your own MCP server'),
    desc: t('用任何语言写一个 MCP server 暴露工具，OryxOS 作为 MCP Client 连接进来，适合接入企业自有系统（ERP / CRM）。', 'Write an MCP server in any language; OryxOS connects as an MCP client. Ideal for wiring in your own systems (ERP / CRM).'),
    badges: ['Python', 'Go', 'Node', 'Any language'],
  },
  {
    icon: '☕',
    title: t('重代码 · Java @Tool Bean', 'Heavy-code · Java @Tool bean'),
    desc: t('用 @Tool 注解写 Java Spring Bean，进程内直接调用，性能最好，适合深度集成和复用现有 Spring 服务。', 'Annotate a Java Spring bean with @Tool for in-process calls — best performance, ideal for deep integration and reusing Spring services.'),
    badges: ['Spring Bean', '@Tool', 'JVM 原生'],
  },
])
</script>

<template>
  <div class="oryx-page">

    <!-- ── HERO ── -->
    <section class="oryx-hero">
      <div class="oryx-hero-inner">
        <div class="oryx-badge">
          <span class="oryx-badge-dot"></span>
          {{ t('企业级 Agent OS · Java 原生', 'Enterprise Agent OS · Java-native') }}
        </div>

        <h1 class="oryx-title">
          <span class="oryx-title-name">Oryx<span class="oryx-title-os">OS</span></span>
        </h1>

        <p class="oryx-title-sub">{{ t('装在你自己机房里的 Agent 统一底座', 'The Agent OS that runs on your own infrastructure') }}</p>

        <p class="oryx-hero-desc">
          {{ t('OryxOS 用 Java 把模型路由、工具调用、记忆、沙箱、渠道接入统一成一个可私有部署的底座。数据不出企业，不锁云生态——业务方写一个目录就能上线一个 Agent。', 'OryxOS unifies model routing, tool calling, memory, sandboxing, and channels into a single self-hosted Java runtime. Your data never leaves your infrastructure, no cloud lock-in — define an agent with one directory.') }}
        </p>

        <div class="oryx-hero-actions">
          <a class="oryx-btn-primary" :href="`${docBase}/what`">
            {{ t('开始使用', 'Get Started') }} →
          </a>
          <a class="oryx-btn-ghost" :href="`${docBase}/architecture`">
            {{ t('架构', 'Architecture') }}
          </a>
          <a class="oryx-btn-ghost" href="https://github.com/oryxos-jin/oryxos" target="_blank" rel="noopener">
            GitHub
          </a>
        </div>

        <div class="oryx-hero-note">
          {{ t('JDK 21 · Spring Boot 3.x · Spring AI Alibaba · MCP · SQLite · 单二进制部署', 'JDK 21 · Spring Boot 3.x · Spring AI Alibaba · MCP · SQLite · single-binary deploy') }}
        </div>
      </div>
    </section>

    <!-- ── PROBLEM ── -->
    <section class="oryx-section">
      <div class="oryx-section-inner">
        <div class="oryx-problem">
          <div class="oryx-problem-text">
            <h2 class="oryx-section-title">{{ t('企业落地 Agent，卡在哪', 'Why enterprise agents stall') }}</h2>
            <p>{{ t('需求侧早已是共识，但大量 Agent 试点走不到生产。真正的难点不在"做出一个 Agent"，在"让它在企业里可控地跑起来"。', 'Demand is settled, yet most agent pilots never reach production. The hard part is not building an agent — it is running it under control inside an enterprise.') }}</p>
            <p class="oryx-problem-item">
              <strong>{{ t('① 数据、合规、可审计过不了关', '① Data, compliance, auditability') }}</strong>
              {{ t('核心数据不能出企业，系统必须完全可审计——SaaS 和绑定公有云的方案直接出局。', 'Core data cannot leave the enterprise and every action must be auditable — SaaS and cloud-locked products are ruled out.') }}
            </p>
            <p class="oryx-problem-item">
              <strong>{{ t('② Java 生态没有 Agent OS 这一层', '② Java has no Agent OS layer') }}</strong>
              {{ t('开源 Agent OS 只有 Node.js 的 OpenClaw、Python 的 Hermes。Java 体系的企业只能在两套技术栈接缝处写胶水。', 'Open-source Agent OSes are Node.js (OpenClaw) or Python (Hermes). Java shops are left writing glue across two stacks.') }}
            </p>
            <p class="oryx-solution-line">{{ t('OryxOS 正好补上这一层：Java 原生、私有部署、day one 可审计。', 'OryxOS fills exactly that layer — Java-native, self-hosted, auditable from day one.') }}</p>
          </div>
          <div class="oryx-problem-compare">
            <div class="oryx-compare-item oryx-compare-bad">
              <div class="oryx-compare-label">{{ t('今天的做法', 'Today') }}</div>
              <div class="oryx-compare-rows">
                <div class="oryx-compare-row"><span class="oryx-compare-icon">✗</span><span>{{ t('SaaS 方案，核心数据出企业', 'SaaS — core data leaves the enterprise') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon">✗</span><span>{{ t('绑定某个公有云，锁生态', 'Locked to one public cloud') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon">✗</span><span>{{ t('用 Node / Python 项目，两套栈写胶水', 'Node / Python projects — glue across two stacks') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon">✗</span><span>{{ t('审计靠事后补，过不了安全审查', 'Auditing bolted on later, fails security review') }}</span></div>
              </div>
            </div>
            <div class="oryx-compare-item oryx-compare-good">
              <div class="oryx-compare-label">OryxOS</div>
              <div class="oryx-compare-rows">
                <div class="oryx-compare-row"><span class="oryx-compare-icon oryx-icon-ok">✓</span><span>{{ t('私有部署，数据不出企业', 'Self-hosted, data stays in-house') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon oryx-icon-ok">✓</span><span>{{ t('Java 原生，复用现有运维链', 'Java-native, reuses your ops toolchain') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon oryx-icon-ok">✓</span><span>{{ t('五大核心能力开箱即用', 'Five core capabilities out of the box') }}</span></div>
                <div class="oryx-compare-row"><span class="oryx-compare-icon oryx-icon-ok">✓</span><span>{{ t('审计 day one 落库，可接 SIEM', 'Audit persisted from day one, SIEM-ready') }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── FLOW DIAGRAM ── -->
    <section class="oryx-section oryx-flow-section">
      <div class="oryx-section-inner">
        <img src="/architecture.svg" alt="OryxOS architecture" class="oryx-flow-img" />
      </div>
    </section>

    <!-- ── CAPABILITIES ── -->
    <section class="oryx-section oryx-primitives-section">
      <div class="oryx-section-inner oryx-primitives-inner">
        <div class="oryx-section-header">
          <div class="oryx-section-tag">{{ t('核心能力', 'Core Capabilities') }}</div>
          <h2 class="oryx-section-title">{{ t('五大核心能力，跑通一个完整 Agent', 'Five core capabilities — a complete agent runtime') }}</h2>
        </div>
        <div class="oryx-primitives">
          <div v-for="p in capabilities" :key="p.title" class="oryx-primitive">
            <div class="oryx-primitive-header">
              <span class="oryx-primitive-icon">{{ p.icon }}</span>
              <div>
                <h3 class="oryx-primitive-title">{{ p.title }}</h3>
                <p class="oryx-primitive-subtitle">{{ p.subtitle }}</p>
              </div>
            </div>
            <pre class="oryx-code"><code>{{ p.code }}</code></pre>
          </div>
        </div>
      </div>
    </section>

    <!-- ── SCENARIOS ── -->
    <section class="oryx-section">
      <div class="oryx-section-inner">
        <div class="oryx-section-header">
          <div class="oryx-section-tag">{{ t('真实场景', 'Real Scenarios') }}</div>
          <h2 class="oryx-section-title">{{ t('八个真实使用场景', 'Eight real-world use cases') }}</h2>
        </div>
        <div class="oryx-scenarios">
          <div v-for="s in scenarios" :key="s.num" class="oryx-scenario">
            <div class="oryx-scenario-num">{{ s.num }}</div>
            <div>
              <h3 class="oryx-scenario-title">{{ s.title }}</h3>
              <p class="oryx-scenario-desc">{{ s.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── PLUGIN TOOL TIERS ── -->
    <section class="oryx-section oryx-sdk-section">
      <div class="oryx-section-inner">
        <div class="oryx-section-header">
          <div class="oryx-section-tag">{{ t('扩展方式', 'Extend') }}</div>
          <h2 class="oryx-section-title">{{ t('三档接入，能低就不高', 'Three tiers — the lower the better') }}</h2>
        </div>
        <div class="oryx-sdk-cards">
          <div v-for="tier in tiers" :key="tier.title" class="oryx-sdk-card" :class="{ 'oryx-sdk-card-featured': tier.featured }">
            <div class="oryx-sdk-card-icon">{{ tier.icon }}</div>
            <h3 class="oryx-sdk-card-title">{{ tier.title }}</h3>
            <p class="oryx-sdk-card-desc">{{ tier.desc }}</p>
            <div class="oryx-sdk-badges">
              <span v-for="b in tier.badges" :key="b" class="oryx-sdk-badge">{{ b }}</span>
            </div>
            <div v-if="tier.note" class="oryx-sdk-note">{{ tier.note }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── API OVERVIEW ── -->
    <section class="oryx-section">
      <div class="oryx-section-inner">
        <div class="oryx-section-header">
          <div class="oryx-section-tag">{{ t('接口总览', 'API') }}</div>
          <h2 class="oryx-section-title">{{ t('核心 10 个 REST 端点', 'The 10 core REST endpoints') }}</h2>
          <p class="oryx-section-desc">{{ t('所有能力通过 REST API 对外暴露，前缀 /api/v1。这是业务系统集成 OryxOS 的唯一通道。', 'Every capability is exposed over REST under /api/v1 — the single channel for integrating OryxOS into business systems.') }}</p>
        </div>
        <div class="oryx-proto-grid">
          <div class="oryx-proto-group">
            <div class="oryx-proto-group-label">{{ t('会话管理', 'Session Management') }}</div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">POST /sessions</code><span class="oryx-proto-desc">{{ t('创建会话', 'Create a session') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">POST /sessions/{id}/messages</code><span class="oryx-proto-desc">{{ t('发消息', 'Send a message') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /sessions/{id}</code><span class="oryx-proto-desc">{{ t('查历史', 'Read history') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">DELETE /sessions/{id}</code><span class="oryx-proto-desc">{{ t('归档会话', 'Archive a session') }}</span></div>
          </div>
          <div class="oryx-proto-group">
            <div class="oryx-proto-group-label">{{ t('Agent 调用与信息', 'Agent & Info') }}</div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">POST /agents/{name}/invoke</code><span class="oryx-proto-desc">{{ t('无状态调用一次 Agent', 'Stateless one-shot invoke') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /profiles</code><span class="oryx-proto-desc">{{ t('列出可用 Agent', 'List available agents') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /memory</code><span class="oryx-proto-desc">{{ t('查长期记忆', 'Read long-term memory') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /tools</code><span class="oryx-proto-desc">{{ t('列出可用工具', 'List available tools') }}</span></div>
          </div>
          <div class="oryx-proto-group">
            <div class="oryx-proto-group-label">{{ t('系统状态', 'System') }}</div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /health</code><span class="oryx-proto-desc">{{ t('健康检查', 'Health check') }}</span></div>
            <div class="oryx-proto-row"><code class="oryx-proto-subject">GET /info</code><span class="oryx-proto-desc">{{ t('运行信息与 Provider 状态', 'Runtime info & provider status') }}</span></div>
          </div>
        </div>
      </div>
    </section>

    <!-- ── CTA ── -->
    <section class="oryx-section oryx-cta-section">
      <div class="oryx-section-inner">
        <div class="oryx-cta">
          <h2 class="oryx-cta-title">{{ t('开始构建', 'Start Building') }}</h2>
          <p class="oryx-cta-desc">{{ t('一个可执行 JAR，装好就跑——无需外部依赖。', 'A single executable JAR — no external dependencies.') }}</p>
          <pre class="oryx-code oryx-cta-code"><code># 1. Build
mvn clean package

# 2. Initialize the workspace
oryxos init

# 3. Configure a key (Profiles use ${ENV_VAR} placeholders)
export DEEPSEEK_API_KEY=sk-xxxxxx

# 4. Chat
oryxos chat --message "Hi, introduce yourself"

# ...or serve the REST API
oryxos serve   # http://localhost:8080</code></pre>
          <div class="oryx-cta-links">
            <a class="oryx-btn-primary" :href="`${docBase}/quick-start`">{{ t('查看文档', 'Read the Docs') }}</a>
            <a class="oryx-btn-ghost" href="https://github.com/oryxos-jin/oryxos" target="_blank" rel="noopener">GitHub</a>
          </div>
        </div>
      </div>
    </section>

    <!-- ── FOOTER ── -->
    <footer class="oryx-footer">
      <div class="oryx-footer-inner">
        <span>OryxOS · {{ t('统一 · 私有 · 易接入 · 可观测', 'Unified · Private · Integrable · Observable') }}</span>
        <span class="oryx-footer-sep">·</span>
        <span>Apache License 2.0</span>
      </div>
    </footer>

  </div>
</template>

<style scoped>
.oryx-page {
  min-height: 100vh;
  background: #ffffff;
  color: #0f172a;
  font-family: inherit;
}

/* ── Hero ── */
.oryx-hero { position: relative; padding: 100px 24px 80px; text-align: center; overflow: hidden; }
.oryx-hero-inner { position: relative; max-width: 780px; margin: 0 auto; display: flex; flex-direction: column; align-items: center; }
.oryx-badge {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 6px 16px; border-radius: 20px;
  border: 1px solid #fed7aa; background: var(--oryx-soft);
  color: var(--oryx-dark); font-size: 12px; font-weight: 600; margin-bottom: 28px;
}
.oryx-badge-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--oryx); animation: pulse 2s infinite; }
@keyframes pulse { 0%,100% { opacity: 1; transform: scale(1); } 50% { opacity: 0.4; transform: scale(1.4); } }
.oryx-title { margin: 0 0 12px; line-height: 1; }
.oryx-title-name {
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: clamp(72px, 14vw, 120px); font-weight: 700; letter-spacing: -0.03em; color: #0f172a;
}
.oryx-title-os { color: var(--oryx); }
.oryx-title-sub { font-size: 18px; color: #64748b; margin: 0 0 20px; }
.oryx-hero-desc { font-size: 16px; line-height: 1.7; color: #475569; max-width: 620px; margin: 0 0 32px; }
.oryx-hero-actions { display: flex; gap: 12px; flex-wrap: wrap; justify-content: center; margin-bottom: 20px; }
.oryx-btn-primary {
  padding: 11px 28px; border-radius: 8px; background: var(--oryx); color: #ffffff;
  font-weight: 600; font-size: 14px; text-decoration: none; transition: background 0.2s, transform 0.15s;
}
.oryx-btn-primary:hover { background: var(--oryx-dark); transform: translateY(-1px); }
.oryx-btn-ghost {
  padding: 11px 28px; border-radius: 8px; border: 1px solid #d4d4d8; color: #334155;
  font-weight: 600; font-size: 14px; text-decoration: none; transition: border-color 0.2s, color 0.2s;
}
.oryx-btn-ghost:hover { border-color: var(--oryx); color: var(--oryx); }
.oryx-hero-note { font-size: 12px; color: #94a3b8; }

/* ── Section ── */
.oryx-section { padding: 72px 24px; }
.oryx-section-inner { max-width: 1000px; margin: 0 auto; }
.oryx-primitives-inner { max-width: 1400px; }
.oryx-section-header { text-align: center; margin-bottom: 48px; }
.oryx-section-tag {
  display: inline-block; font-size: 11px; font-weight: 700; letter-spacing: 0.1em;
  text-transform: uppercase; color: var(--oryx-dark);
  padding: 4px 12px; border-radius: 20px; border: 1px solid #fed7aa; background: var(--oryx-soft); margin-bottom: 14px;
}
.oryx-section-title { font-size: clamp(22px, 4vw, 32px); font-weight: 700; color: #0f172a; margin: 0 0 12px; }
.oryx-section-desc { font-size: 15px; color: #64748b; max-width: 640px; margin: 0 auto; line-height: 1.6; }

/* ── Problem ── */
.oryx-problem { display: grid; grid-template-columns: 1fr 1fr; gap: 48px; align-items: start; }
.oryx-problem-text p { color: #64748b; line-height: 1.7; margin: 0 0 14px; font-size: 15px; }
.oryx-problem-item strong { color: #0f172a; display: block; margin-bottom: 4px; }
.oryx-solution-line { color: #0f172a !important; font-weight: 600; }
.oryx-problem-compare { display: flex; flex-direction: column; gap: 16px; }
.oryx-compare-item { padding: 20px; border-radius: 12px; border: 1px solid #e5e7eb; }
.oryx-compare-bad { background: #fafafa; }
.oryx-compare-good { background: var(--oryx-soft); border-color: #fed7aa; }
.oryx-compare-label { font-size: 11px; font-weight: 700; color: #94a3b8; margin-bottom: 12px; text-transform: uppercase; letter-spacing: 0.08em; }
.oryx-compare-good .oryx-compare-label { color: var(--oryx-dark); }
.oryx-compare-rows { display: flex; flex-direction: column; gap: 8px; }
.oryx-compare-row { display: flex; align-items: flex-start; gap: 10px; font-size: 13px; color: #475569; line-height: 1.5; }
.oryx-compare-icon { flex-shrink: 0; font-style: normal; color: #cbd5e1; font-weight: 700; width: 14px; }
.oryx-icon-ok { color: var(--oryx); }

/* ── Primitives ── */
.oryx-primitives-section { background: #f8fafc; }
.oryx-primitives { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); grid-auto-rows: 1fr; gap: 16px; }
.oryx-primitive {
  padding: 20px; border-radius: 14px; border: 1px solid #e5e7eb; background: #ffffff;
  display: flex; flex-direction: column; gap: 12px; transition: border-color 0.2s, box-shadow 0.2s; min-width: 0; overflow: hidden;
}
.oryx-primitive .oryx-code { flex: 1; }
.oryx-primitive:hover { border-color: var(--oryx); box-shadow: 0 4px 16px rgba(234, 88, 12, 0.10); }
.oryx-primitive-header { display: flex; align-items: flex-start; gap: 12px; }
.oryx-primitive-icon { font-size: 28px; flex-shrink: 0; }
.oryx-primitive-title { font-size: 17px; font-weight: 700; color: #0f172a; margin: 0 0 2px; }
.oryx-primitive-subtitle { font-size: 12px; color: #94a3b8; margin: 0; }
.oryx-code {
  background: #f8fafc; border: 1px solid #e5e7eb; border-radius: 8px; padding: 14px 16px;
  font-size: 12px; line-height: 1.6; color: #334155; overflow-x: auto; margin: 0; white-space: pre;
}
.oryx-code code { font-family: 'JetBrains Mono', 'Fira Code', monospace; background: none; color: inherit; }

/* ── Scenarios ── */
.oryx-scenarios { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
.oryx-scenario { display: flex; gap: 16px; padding: 20px; border-radius: 12px; border: 1px solid #e5e7eb; background: #fafafa; }
.oryx-scenario-num { font-size: 28px; font-weight: 800; color: #fdba74; line-height: 1; flex-shrink: 0; font-variant-numeric: tabular-nums; }
.oryx-scenario-title { font-size: 15px; font-weight: 600; color: #0f172a; margin: 0 0 6px; }
.oryx-scenario-desc { font-size: 13px; color: #64748b; line-height: 1.6; margin: 0; }

/* ── Tiers (SDK) ── */
.oryx-sdk-section { background: #f8fafc; }
.oryx-sdk-cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.oryx-sdk-card { background: #ffffff; border: 1px solid #e5e7eb; border-radius: 16px; padding: 28px 24px; display: flex; flex-direction: column; gap: 12px; position: relative; }
.oryx-sdk-card-featured { border-color: var(--oryx); box-shadow: 0 4px 20px rgba(234, 88, 12, 0.10); }
.oryx-sdk-card-icon { font-size: 28px; }
.oryx-sdk-card-title { font-size: 17px; font-weight: 700; color: #0f172a; margin: 0; }
.oryx-sdk-card-desc { font-size: 14px; color: #64748b; line-height: 1.6; margin: 0; flex: 1; }
.oryx-sdk-badges { display: flex; flex-wrap: wrap; gap: 8px; }
.oryx-sdk-badge { padding: 3px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; background: #f1f5f9; border: 1px solid #e2e8f0; color: #475569; }
.oryx-sdk-note { font-size: 12px; font-weight: 700; color: var(--oryx-dark); }
.oryx-sdk-card-featured .oryx-sdk-badge { background: var(--oryx-soft); border-color: #fed7aa; color: var(--oryx-dark); }

/* ── Protocol / API grid ── */
.oryx-proto-grid { display: flex; flex-direction: column; gap: 28px; }
.oryx-proto-group { display: flex; flex-direction: column; gap: 6px; }
.oryx-proto-group-label { font-size: 11px; font-weight: 700; letter-spacing: 0.1em; text-transform: uppercase; color: var(--oryx-dark); margin-bottom: 4px; }
.oryx-proto-row { display: flex; align-items: baseline; gap: 16px; padding: 8px 14px; border-radius: 8px; background: #fafafa; border: 1px solid #e5e7eb; flex-wrap: wrap; }
.oryx-proto-subject { font-family: 'JetBrains Mono', 'Fira Code', monospace; font-size: 12px; color: var(--oryx-dark); background: var(--oryx-soft); border: 1px solid #fed7aa; padding: 2px 8px; border-radius: 4px; flex-shrink: 0; white-space: nowrap; }
.oryx-proto-desc { font-size: 13px; color: #64748b; flex: 1; }

/* ── CTA ── */
.oryx-cta-section { background: #f8fafc; }
.oryx-cta { text-align: center; max-width: 700px; margin: 0 auto; }
.oryx-cta-title { font-size: 28px; font-weight: 700; color: #0f172a; margin: 0 0 12px; }
.oryx-cta-desc { font-size: 15px; color: #64748b; margin: 0 0 24px; }
.oryx-cta-code { text-align: left; margin-bottom: 28px; }
.oryx-cta-links { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }

/* ── Flow diagram ── */
.oryx-flow-section { padding: 0 24px 72px; }
.oryx-flow-img { width: 100%; display: block; border: 1px solid #e5e7eb; border-radius: 12px; background: #fff; }

/* ── Footer ── */
.oryx-footer { border-top: 1px solid #e5e7eb; padding: 28px 24px; text-align: center; }
.oryx-footer-inner { font-size: 13px; color: #94a3b8; display: inline-flex; gap: 10px; flex-wrap: wrap; justify-content: center; }
.oryx-footer-sep { color: #cbd5e1; }

/* ── Responsive ── */
@media (max-width: 900px) { .oryx-sdk-cards { grid-template-columns: 1fr; } }
@media (max-width: 768px) {
  .oryx-hero { padding: 72px 20px 60px; }
  .oryx-problem { grid-template-columns: 1fr; }
  .oryx-primitives { grid-template-columns: 1fr; }
  .oryx-scenarios { grid-template-columns: 1fr; }
  .oryx-section { padding: 48px 20px; }
}
</style>
