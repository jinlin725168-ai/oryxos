package com.oryxos.memory;

import org.springframework.stereotype.Service;

/**
 * 三层记忆的统一门面（技术方案 §5.1）。对 ReAct 循环只暴露一个接口，
 * 内部把会话记忆委托给 SessionManager、长期记忆委托给 LongTermMemoryStore。
 * <p>核心阶段骨架：US-3 实现会话 + 长期两层。
 */
@Service
public class MemoryService {

    /** 组装 prompt 时取完整上下文（会话历史 + 长期记忆）。 */
    public String loadContext(String sessionId) {
        // TODO(US-3): SessionManager + LongTermMemoryStore.load() 合并
        throw new UnsupportedOperationException("MemoryService 待 US-3 实现");
    }
}
