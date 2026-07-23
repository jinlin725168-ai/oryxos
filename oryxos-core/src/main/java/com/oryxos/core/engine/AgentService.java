package com.oryxos.core.engine;

import com.oryxos.core.session.Session;
import org.springframework.stereotype.Service;

/**
 * 三种触发源（CLI 人推 / Web Service 人推 / AgentScheduler 钟推）共用的统一入口
 * （技术方案 §4.2）。process 内部编排：放 ProfileContext → 跑 ReActLoop → 持久化 Session → 清理。
 * <p>核心阶段骨架。
 */
@Service
public class AgentService {

    private final ReActLoop reActLoop;

    public AgentService(ReActLoop reActLoop) {
        this.reActLoop = reActLoop;
    }

    public String process(Session session, String message) {
        // TODO(US-2/US-5): ProfileContext 入栈 → reActLoop.run → 持久化 → finally 清理
        return reActLoop.run(session, message);
    }
}
