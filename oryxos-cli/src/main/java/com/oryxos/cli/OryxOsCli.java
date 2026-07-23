package com.oryxos.cli;

import picocli.CommandLine.Command;

/**
 * Picocli 命令行入口（技术方案 §8.7）。核心阶段 12 个子命令：
 * init / status / chat / serve / gateway / profile(list·create·show·delete) /
 * provider list / tool list / session list。
 * <p>骨架版：命令树先立，各子命令实现见研发阶段。
 */
@Command(
        name = "oryxos",
        mixinStandardHelpOptions = true,
        version = "OryxOS 1.0.0-SNAPSHOT",
        description = "企业级 Java 原生 Agent OS",
        subcommands = {
                OryxOsCli.Init.class,
                OryxOsCli.Chat.class,
                OryxOsCli.Serve.class
        })
public class OryxOsCli implements Runnable {

    @Override
    public void run() {
        System.out.println("OryxOS —— 用 `oryxos --help` 查看命令。");
    }

    @Command(name = "init", description = "初始化 .oryxos/ 工作区")
    static class Init implements Runnable {
        @Override public void run() { /* TODO(US-1): InitCommand */ }
    }

    @Command(name = "chat", description = "交互对话")
    static class Chat implements Runnable {
        @Override public void run() { /* TODO(US-2): CliChannel */ }
    }

    @Command(name = "serve", description = "启动 HTTP API 服务")
    static class Serve implements Runnable {
        @Override public void run() { /* TODO(US-5): WebServer */ }
    }
}
