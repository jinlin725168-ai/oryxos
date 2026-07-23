package com.oryxos.core.tool;

/**
 * Tool 执行结果（技术方案 6.1）：成功标识、结果内容、错误信息、是否可重试。
 */
public record ToolResult(boolean success, String content, String errorMessage, boolean retryable) {

    public static ToolResult ok(String content) {
        return new ToolResult(true, content, null, false);
    }

    public static ToolResult error(String message, boolean retryable) {
        return new ToolResult(false, null, message, retryable);
    }
}
