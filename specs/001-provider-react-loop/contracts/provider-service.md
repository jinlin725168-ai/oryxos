# Contract: ProviderService（LLM 调用抽象）

`ProviderService` 是 ReAct 循环与各家大模型之间的唯一边界（宪法 III/IV）。

## 接口意图

```text
ChatResult chat(String providerName, Prompt prompt, List<OryxTool> availableTools)
```

- 按 `providerName` 从**显式映射** `Map<String, ChatModel>` 取实例（宪法 IV），MUST NOT 靠类型扫描。
- 把 `availableTools` 经 `FunctionCallingAdapter` 转成模型可识别的工具声明，随 `prompt` 传入。
- **只调一次模型**（`ChatModel.call`），MUST NOT 触发 Spring AI 的自动 tool 执行（宪法 III）。
- 返回 `ChatResult`：要么是最终文本，要么是模型要求的 `toolCalls`（供 `ReActLoop` 决定下一步）。

## ChatResult 形态

| 字段 | 说明 |
|------|------|
| `content` | 模型输出文本（无工具调用时即最终回答） |
| `toolCalls` | `List<ToolCall>`：模型要求调用的工具（可空） |
| `usage` | `promptTokens/completionTokens/totalTokens`（供审计日志） |

## 配置契约（application.yaml）

```yaml
oryxos:
  providers:
    - name: deepseek
      base-url: https://api.deepseek.com
      model: deepseek-chat
      api-key: ${DEEPSEEK_API_KEY}
    - name: zhipu
      base-url: https://open.bigmodel.cn/api/paas/v4
      model: glm-4
      api-key: ${ZHIPU_API_KEY}
```

> base-url/model 以实现时核对为准；两家均走 OpenAI 兼容协议，由 Spring AI Alibaba 吸收差异。

## 不变式

- 更换 `providerName`（deepseek ↔ zhipu）MUST NOT 改变对话与工具调用行为（FR-007、SC-004）。
- LLM 调用失败 MUST 以清晰错误上抛（FR-009），核心阶段不做自动切换。
- 每次调用 MUST 产出一条 LlmCall 审计日志事件（含 usage、耗时；不含凭证）。
