package com.oryxos.channel.cli;

import com.oryxos.core.engine.AgentService;
import org.springframework.stereotype.Component;

/**
 * CLI Channel（技术方案 §8.4）：oryxos chat 的实现，读 stdin 写 stdout 做交互式对话，
 * 每次输入调 AgentService.process，支持 /quit 退出。
 * <p>核心阶段骨架。
 */
@Component
public class CliChannel {

    private final AgentService agentService;

    public CliChannel(AgentService agentService) {
        this.agentService = agentService;
    }

    /** 启动交互循环。 */
    public void start() {
        // TODO(US-2): 读 stdin → AgentService.process → 写 stdout，/quit 退出
        throw new UnsupportedOperationException("CliChannel 待 US-2 实现");
    }
}
