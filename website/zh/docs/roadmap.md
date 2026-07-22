# 路线图

OryxOS 分三阶段演进——**核心阶段是地基，企业级治理是终局**。

## ✅ 核心阶段：单机运行时内核

五大核心能力跑通，对齐业界开源 Agent OS 基础层：

- 对接 LLM（Provider 抽象）
- ReAct 循环（自实现）
- Memory 三层记忆（会话 + 长期 `MEMORY.md`）
- Plugin Tool（内置 9 个 + 三档接入）
- Web Service（核心 10 个 REST 端点）
- CLI（12 个命令）、定时任务、Session 持久化、审计落库、项目主页

**发布硬条件**：三个每日自动运行的端到端 Demo（每日天气、每日科技日报、每日 GitHub 日报）跑通。

## 🔜 扩展阶段：生产级 + 治理

> 这一层是 OryxOS 区别于个人级 Agent OS 的核心差异化所在。

- **渠道与模型**：企业微信 / 飞书 / 钉钉 / Slack Channel；Provider Fallback 与 hedge racing；Adaptive Routing
- **记忆与能力**：Memory 自动抽取；语义向量检索（LanceDB / pgvector / JVector）；情景记忆；Memory Wiki
- **工具与安全**：Tool Policy；OryxOS 作为 MCP server 暴露；完整 Sandbox（Docker / K8s / microVM）
- **治理与运维**：Web 管理台；**SSO 与多租户 RBAC**（SAML/OIDC，对接 AD/Okta/Entra ID）；完整审计与 SIEM 导出；集群化部署与高可用
- **企业集成**：ERP / CRM / CMDB / 监控系统现成 connector

## 🌐 社区共建

不规定时间表，作为长期方向开放给社区贡献：

- Skills Marketplace（兼容 agentskills.io）
- 多语言 SDK（Java → Python → TypeScript → Go）
- 可视化 Profile 编辑器
- Kubernetes Operator、边缘部署（GraalVM Native Image）
- Voice Channel、移动端管理台

## 更远：从单机到分布式 Agent 协作

单机做扎实后，通过"实例无状态 + 状态外置"走向多实例高可用；更远期是**分布式 Agent 协作**——让分散在不同部门、机器甚至组织的 Agent 跨节点互相发现、可靠委托、协同完成横跨多方的业务。

| 阶段 | 形态 | 重点 |
|------|------|------|
| 阶段一（当前） | 单机私有部署 | 完整运行时内核 |
| 阶段二（中期） | 底座分布式部署 | 多实例 + 外置状态，高可用 |
| 阶段三（远期） | 分布式 Agent 协作 | 跨节点 / 跨组织互发现、互委托 |
