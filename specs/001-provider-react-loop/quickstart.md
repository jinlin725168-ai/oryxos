# Quickstart / 验收指南：Provider 抽象 + ReAct 工具调用对话

用于**证明本特性端到端可用**的可复现步骤。实现细节见 `tasks.md` 与代码，不在此重复。

## 前置条件

- JDK 21、Maven 3.9+
- 两个 Provider 的 key（任一即可跑主流程；验证"模型无关"需两个都有）：
  ```bash
  export DEEPSEEK_API_KEY=sk-xxxx
  export ZHIPU_API_KEY=xxxx.xxxx      # 智谱 GLM
  ```
- 可访问公网（放行 open-meteo 两个域名）

## 构建与初始化

```bash
mvn -q clean package
java -jar oryxos-boot/target/oryxos.jar   # 或后续的 `oryxos` 包装脚本
oryxos init                               # 生成 .oryxos/（含默认 assistant Agent 目录）
```

> `.oryxos/agents/assistant/AGENT.md`：frontmatter 至少含 `provider.name`、`model`、`tools: [http_get]`；
> 正文（零代码 skill）写明"查天气用 http_get 两步查 open-meteo（geocoding→forecast）"。
> `application.yaml` 的 `oryxos.providers` 配好
> deepseek/zhipu（见 `contracts/provider-service.md`）。`http.allowed_domains` 放行
> `api.open-meteo.com`、`geocoding-api.open-meteo.com`。

## 场景 1 — 工具调用闭环（US-1 / SC-001,002）

```bash
oryxos chat --message "查一下北京天气并告诉我今天穿什么"
```
**预期**：Agent 自动用 `http_get` 两步查 open-meteo、拿到数据，返回结合天气的穿衣建议；用户未手动执行
任何工具。日志中可见 ≥1 条 ToolInvocation（`http_get`，含 geocoding/forecast 两步，success）+ ≥1 条
LlmCall 事件。

## 场景 2 — 无需工具直接作答（US-1 / FR-005）

```bash
oryxos chat --message "用一句话解释什么是 ReAct"
```
**预期**：直接作答，**无** ToolInvocation 日志事件。

## 场景 3 — 多轮上下文（US-2 / SC-003）

```bash
oryxos chat          # 进入交互
> 北京天气怎么样
> 那适合穿什么       # 不重复"北京"
> /quit
```
**预期**：第二问基于第一问的天气结果作答，无需重复城市。

## 场景 4 — 模型无关（US-3 / SC-004）

把默认 Agent 的 `provider.name` 在 `deepseek` 与 `zhipu` 间切换，各跑一次场景 1。
**预期**：两者都能完成工具调用并给出合理回答，用法无任何改动。

## 场景 5 — 失败与边界（SC-005 / FR-004,008,009,010）

- 断网或临时把白名单域名改错 → 天气工具失败被回填，Agent 如实告知"暂时拿不到天气"，对话不崩。
- 故意让模型陷入反复调工具 → 达到 `maxIterations`（10）强制作答，无死循环。
- 清空 `DEEPSEEK_API_KEY` 后用 deepseek → 启动/调用时清晰报错，指明缺失的环境变量。
- 让工具请求一个不在白名单的域名 → 被 `WhitelistSandbox` 拒绝，原因回填。

## 通过标准

- 场景 1–4 全部符合预期；场景 5 的四个失败路径都给出**明确反馈**而非崩溃/卡死。
- 审计日志包含对齐字段的 LlmCall / ToolInvocation 事件，且**不含**任何明文 key。
