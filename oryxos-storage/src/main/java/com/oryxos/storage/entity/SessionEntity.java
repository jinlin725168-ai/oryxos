package com.oryxos.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * sessions 表实体（技术方案 §9.2）。骨架版仅列关键字段。
 */
@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "profile_name")
    private String profileName;

    private String channel;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "messages_json", columnDefinition = "TEXT")
    private String messagesJson;

    private String status;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessagesJson() { return messagesJson; }
    public void setMessagesJson(String messagesJson) { this.messagesJson = messagesJson; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
