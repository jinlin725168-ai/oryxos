package com.oryxos.core.session;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户和 Agent 一次对话的上下文容器（需求文档 §2、技术方案 §9.2）。
 * 核心阶段骨架，字段与 sessions 表对应。
 */
public class Session {

    private String sessionId;
    private String profileName;
    private String channel;
    private String userId;
    private final List<String> messages = new ArrayList<>();

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getMessages() { return messages; }
}
