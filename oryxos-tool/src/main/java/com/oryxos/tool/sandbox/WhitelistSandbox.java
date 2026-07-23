package com.oryxos.tool.sandbox;

import org.springframework.stereotype.Component;

/**
 * 核心阶段唯一沙箱实现（技术方案 §6.7）：应用层白名单校验。
 * 文件路径白名单 / Shell 命令白名单 / HTTP 域名白名单，配置在 application.yaml。
 * 不使用 Java SecurityManager（JDK 17 起废弃、JDK 21 已不可用）。
 */
@Component
public class WhitelistSandbox implements Sandbox {

    @Override
    public void enforce(SandboxAction action) {
        // TODO(US-2/US-4): 按 ActionType 路由到 checkFilePath / checkShellCommand / checkHttpUrl
        switch (action.type()) {
            case FILE_READ, FILE_WRITE -> checkFilePath(action.target());
            case SHELL_COMMAND -> checkShellCommand(action.target());
            case HTTP_REQUEST -> checkHttpUrl(action.target());
        }
    }

    private void checkFilePath(String target) { /* TODO: 路径标准化 + 白名单，防 ../ 穿越 */ }

    private void checkShellCommand(String target) { /* TODO: 拆首个 token 比对白名单 */ }

    private void checkHttpUrl(String target) { /* TODO: 解析 host 做通配符匹配 */ }
}
