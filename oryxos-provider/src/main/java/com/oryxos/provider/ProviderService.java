package com.oryxos.provider;

import org.springframework.stereotype.Service;

/**
 * 核心能力一：LLM 调用的统一抽象（技术方案 §3）。
 * 对 ReAct 循环屏蔽不同 LLM 厂商差异，维护 provider-name → ChatModel 的<b>显式映射</b>
 * （不靠类型扫描区分多 Provider）。
 * <p>核心阶段骨架：US-1 基于 Spring AI Alibaba 的 ChatClient 实现。
 */
@Service
public class ProviderService {

    /** 按 Profile 选定的 provider name 调用对应模型。 */
    public String chat(String providerName, String prompt) {
        // TODO(US-1): 从显式映射表取 ChatModel，走 Spring AI 协议转换完成调用，并落 llm_calls 审计
        throw new UnsupportedOperationException("ProviderService 待 US-1 实现");
    }
}
