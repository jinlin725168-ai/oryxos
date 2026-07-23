package com.oryxos.core.profile;

import java.util.List;

/**
 * Agent 的运行时宿主配置（需求文档 §2）。核心阶段由 AGENT.md frontmatter 派生。
 * 骨架版仅列关键字段。
 */
public record Profile(
        String name,
        String description,
        String providerName,
        String model,
        List<String> tools,
        int maxIterations,
        int maxHistoryTurns) {
}
