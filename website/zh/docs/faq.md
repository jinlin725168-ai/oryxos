# 常见问题

## OryxOS 和 OpenClaw、Hermes 有什么区别？

同类不同定位。三者都是 Agent OS：OpenClaw（Node.js）偏个人、Hermes（Python）偏小团队，**OryxOS 直接定位严监管企业场景**，Java 原生、私有部署、day one 可审计。三者通过 `SKILL.md` 兼容，社区优质 Skill 经企业审查后理论上可导入 OryxOS，生态互补不竞争。

## OryxOS 和 Dify、Coze 是竞争关系吗？

不是，是互补。Dify 编排的是"流程"（可拖拽 workflow），OryxOS 承载的是"常驻的 Agent"。如果业务需要复杂 workflow，可以用 Dify 在 OryxOS 之上跑（Dify 作应用层调 OryxOS 的 API）。

## OryxOS 和 Spring AI 是什么关系？

复用。OryxOS 的 LLM Provider 抽象直接基于 Spring AI Alibaba 的主流 LLM connector，不重复造轮子。但 OryxOS **只用** Spring AI 的协议转换和 schema 生成，**禁用其自动 tool 执行**——tool 调度完全由自实现的 `ReActLoop` + `ToolExecutor` 控制。

## 为什么核心阶段不用向量数据库？

LanceDB 的 Java 本地嵌入式支持还在开发中，其他向量库（Qdrant、Milvus）需要外部进程，pgvector 要外部 PostgreSQL——都不符合"单二进制部署"的定位。核心阶段先用 SQLite + `MEMORY.md` + 关键词检索跑通最短链路，语义检索放扩展阶段，接口已预留升级空间。

## 数据会外发吗？

不会。OryxOS 装在企业自己的基础设施上，**本身不收集任何企业数据**。所有 Session、记忆、审计数据都留在本地。LLM 调用走企业自己配置的 Provider（也可接本地推理 Ollama / vLLM，数据完全不出企业）。

## 支持哪些部署方式？

物理机、虚拟机、Docker、Kubernetes 都支持。核心阶段是单个可执行 fat JAR（`java -jar` 启动），扩展阶段通过 GraalVM Native Image 编译成原生二进制，把启动降到 100ms 以下。

## 核心阶段的安全边界是什么？

核心阶段用**应用层白名单**做基础隔离：文件路径白名单、Shell 命令白名单、HTTP 域名白名单，加执行超时。这是"劝阻级"防线，防的是模型犯傻误操作，**防不住蓄意绕过**——不建议核心阶段在生产跑完全不可信的代码。完整的容器级 / microVM 沙箱、鉴权、SSO 放在扩展阶段。

## 兼容哪些环境？

- **JDK**：21 及以上
- **操作系统**：Linux 主流发行版（Ubuntu 22.04+、CentOS 8+、Debian 11+、Alibaba Cloud Linux 3、Rocky Linux）
- **LLM 协议**：OpenAI 兼容协议是事实标准，只要 Provider 实现这套协议就能直接接
