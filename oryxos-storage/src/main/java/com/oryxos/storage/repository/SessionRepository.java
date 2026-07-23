package com.oryxos.storage.repository;

import com.oryxos.storage.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Session 持久化仓库（技术方案 §9.2）。核心阶段 SQLite，跨重启恢复。
 */
public interface SessionRepository extends JpaRepository<SessionEntity, String> {
}
