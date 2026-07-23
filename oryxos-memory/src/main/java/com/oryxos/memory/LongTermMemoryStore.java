package com.oryxos.memory;

/**
 * 长期记忆的可插拔后端接口（技术方案 §5.1，"接口墙"）。
 * 三档实现：MarkdownMemoryStore（默认，MEMORY.md）、SqliteMemoryStore、Mem0MemoryStore。
 * 行为契约：不缓存、核心区永不截断、写核心/归档由 scope 显式指定、recall 为关键词检索。
 */
public interface LongTermMemoryStore {

    enum MemoryScope { CORE, ARCHIVAL }

    void append(String content, MemoryScope scope);

    /** 返回核心区全量 + 归档区截断后的内容。 */
    String load();

    /** 按关键词检索，只在归档区匹配。 */
    String recallByKeyword(String query);
}
