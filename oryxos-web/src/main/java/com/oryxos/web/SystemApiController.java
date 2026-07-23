package com.oryxos.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 系统状态端点（技术方案 §7.2）：GET /health、GET /info。
 * 其余 5 个 Controller（Session/Agent/Profile/Memory/Tool）在 US-5 补齐。
 * <p>骨架版：/health 返回可用，便于最先跑通 Web 层。
 */
@RestController
@RequestMapping("/api/v1")
public class SystemApiController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("code", 0, "message", "success", "data", Map.of("status", "UP"));
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of("code", 0, "message", "success",
                "data", Map.of("name", "OryxOS", "version", "1.0.0-SNAPSHOT", "stage", "core"));
    }
}
