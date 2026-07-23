package com.oryxos.core.tool;

/**
 * OryxOS 内部统一的 Tool 抽象接口（技术方案 6.1）。
 * 内置 Tool、@Tool 注解的 Plugin Tool、MCP Tool 都被包装成 OryxTool。
 * <p>核心阶段骨架：签名先立，具体实现见研发阶段。
 */
public interface OryxTool {

    String getName();

    String getDescription();

    /** JSON Schema 描述入参。 */
    String getInputSchema();

    /** 接收 JSON 输入，返回 ToolResult。 */
    ToolResult execute(String jsonInput);
}
