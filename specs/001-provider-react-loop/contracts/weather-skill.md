# Contract: 天气能力（零代码 skill，非 Java 工具）

天气不是一个专用 Java 工具，而是**用默认 Agent 的 `AGENT.md` 指令 + 通用 `http_get`** 组合出来的能力
（宪法 V「一个目录=一个 Agent」+ 工具三档取最低）。OryxOS 不解析任务步骤，由 LLM 按指令自己两步组合。

## 载体

`.oryxos/agents/assistant/AGENT.md`：
- frontmatter：`tools: [http_get]`（外加 provider/model）
- 正文（即"天气 skill"）：指导 LLM 如何用 `http_get` 完成"按城市查天气"。

> 若后续引入 `read_file`，可把这段指令拆到 `skills/weather.md` 走渐进式披露；本特性直接写在 `AGENT.md`
> 正文即可（正文由 `ContextLoader` 注入 system prompt，无需额外工具）。

## AGENT.md 正文应包含的指令要点

1. **第一步 geocoding**：用 `http_get` 请求
   `https://geocoding-api.open-meteo.com/v1/search?name={城市}&count=1&language=zh`，
   从结果取 `latitude` / `longitude`。
2. **第二步 forecast**：用 `http_get` 请求
   `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&daily=temperature_2m_max,temperature_2m_min,weathercode&timezone=auto`，
   取当日温度区间与天气代码。
3. **综合作答**：把温度/天气翻译成自然语言，结合用户诉求（如穿衣建议）给最终回答。
4. **失败处理**：任一 `http_get` 失败时如实告知"暂时拿不到天气"，不杜撰数据（对应 FR-008）。

## 依赖底座（不在本 skill 内实现）

- `http_get`（内置工具，T017）执行前过 `Sandbox.enforce(HTTP_REQUEST, url)`。
- HTTP 白名单（T003）必须放行 `geocoding-api.open-meteo.com`、`api.open-meteo.com`，否则被
  `WhitelistSandbox` 拒绝（FR-010）。
- open-meteo 免注册 key。

## 验收

验收判定"是否成功获取并综合了天气"，**不锁死**具体温度数值（可稳定复现）。见 `quickstart.md` 场景 1/2。
