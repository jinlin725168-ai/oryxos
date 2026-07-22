# OryxOS 是什么

**OryxOS 是用 Java 实现、面向企业场景的 Agent OS（智能体操作系统底座）。** 它装在企业自己的 K8s、服务器或物理机上，作为统一底座在其上跑各种业务 Agent（运维、客服、HR、销售、知识管理），让它们共享一套**渠道接入、模型路由、工具调用、记忆系统、沙箱执行**能力。

数据完全留在企业自己的基础设施上，**不外发、不锁任何云生态**。业务方只需写一个 Agent 目录 + 配置工具，就能上线一个新 Agent——**不需要写 Agent 后端代码**。

## Agent OS ≠ 框架 / 编排平台

| | 产物 | 谁来用 | 跑在哪 |
|---|---|---|---|
| **框架**（Spring AI、LangChain4j） | 代码 / SDK | 开发者写代码 | 自己搭运行环境 |
| **编排平台**（Dify、Coze） | 可拖拽的 workflow | 业务/开发者 | 平台运行时之上 |
| **Agent OS（OryxOS）** | 配置出来的常驻 Agent | 业务方配置 + 开发者写 Tool | **用户自己的机器 / K8s** |

一句话：框架给你材料要你自己盖房子，编排平台给你流程跑在运行时之上，**OryxOS 给你运行时本身**——一个让 Agent 能常驻、可治理、可审计地跑起来的底座。三者是复用与互补关系，不是竞争。

## 为什么是 Java

开源 Agent OS 领域，`OpenClaw`（Node.js）偏个人、`Hermes`（Python）偏小团队，**Java 生态在 Agent OS 这一层完全空白**。而企业现有的 ERP、CRM、CMDB、SSO、监控大量是 Java 接口，Spring AI Alibaba 也已把底层 LLM 调用解决——缺的就是上面那一层"Agent OS"。OryxOS 来填这个位置：

- **Spring Boot 是企业后端的事实标准**，装 OryxOS 像装一个 Spring Boot 应用一样自然
- **复用 JVM 运维工具链**（Nacos、Sentinel、SkyWalking、Arthas、Prometheus）
- **跟企业现有 Java 系统对接成本最低**，Tool 直接调现有 Spring Bean
- **严监管行业的私有部署要求让 Java 成为确定性选择**

## 交付分两段

OryxOS 的交付节奏分两段——**核心阶段是地基，企业级治理是终局**：

1. **核心阶段**：先用 Java 把 Agent OS 的运行时内核做扎实，能力上对齐业界开源 Agent OS 基础层
2. **扩展阶段 + 社区共建**：真正的差异化治理层（多租户、SSO、完整审计、Tool 治理）在核心内核之上陆续补齐

## 四个设计目标

- **统一**：企业内多个 Agent 共享一套底座
- **私有**：数据和部署完全在企业自己手里
- **易接入**：标准 Spring Boot 工程结构，跟现有系统和工具链直接对接
- **可观测**：Prometheus 指标、结构化日志、健康检查、Web 管理台

下一步 → [快速开始](./quick-start) ｜ [五大核心能力](./capabilities)
