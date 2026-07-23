package com.oryxos.tool.sandbox;

/**
 * 沙箱抽象接口（技术方案 §6.7，"接口先行"）。
 * 只表达"在受控环境里执行一个动作"，不携带任何一档实现特有的概念。
 * 核心阶段唯一实现 WhitelistSandbox；扩展阶段按信号升级到容器 / microVM，接口不变。
 */
public interface Sandbox {

    enum ActionType { FILE_READ, FILE_WRITE, SHELL_COMMAND, HTTP_REQUEST }

    record SandboxAction(ActionType type, String target) {}

    /** 校验失败抛 SandboxViolationException，Tool 执行终止。 */
    void enforce(SandboxAction action);
}
