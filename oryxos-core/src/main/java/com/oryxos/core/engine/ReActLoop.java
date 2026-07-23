package com.oryxos.core.engine;

import com.oryxos.core.session.Session;
import org.springframework.stereotype.Component;

/**
 * ReAct 循环引擎（技术方案 §4）——OryxOS 最核心的一段代码。
 * reason → act → observe → repeat，达到最大迭代次数强制结束。
 * <p>核心阶段骨架：真正的循环在 US-2 实现，自实现、不依赖 Spring AI 的 Agent 抽象。
 */
@Component
public class ReActLoop {

    public static final int DEFAULT_MAX_ITERATIONS = 10;

    /** 输入 Session 和用户消息，输出 Agent 最终响应。 */
    public String run(Session session, String userMessage) {
        // TODO(US-2): 组装 Prompt → 调 Provider → 解析 Tool 调用 → ToolExecutor 执行 → 回填 → 循环
        throw new UnsupportedOperationException("ReActLoop 待 US-2 实现");
    }
}
