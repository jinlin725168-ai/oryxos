package com.oryxos.tool;

import com.oryxos.core.tool.OryxTool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一管理所有 Tool（技术方案 §6.6）。
 * 启动时扫描 @Tool 注解方法（内置 + 方式三）+ MCP Client 注册的工具（方式二），
 * 全部包装成 OryxTool 注册进来；Profile 按 tools 字段过滤可用子集。
 * <p>核心阶段骨架。
 */
@Component
public class ToolRegistry {

    private final Map<String, OryxTool> tools = new ConcurrentHashMap<>();

    public void register(OryxTool tool) {
        tools.put(tool.getName(), tool);
    }

    public OryxTool get(String name) {
        return tools.get(name);
    }

    public List<OryxTool> all() {
        return List.copyOf(tools.values());
    }
}
