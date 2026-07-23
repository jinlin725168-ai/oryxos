package com.oryxos.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Picocli 命令行入口（技术方案 §8.7）——整个 OryxOS 的 main 函数。
 * 核心阶段 12 个子命令：init / status / chat / serve / gateway /
 * profile(list·create·show·delete) / provider list / tool list / session list。
 * <p>骨架版：命令树先立，`--version` 与无参运行均打印 OryxOS 版本信息；各子命令实现见研发阶段。
 */
@Command(
        name = "oryxos",
        mixinStandardHelpOptions = true,
        versionProvider = OryxOsCli.VersionProvider.class,
        description = "企业级 Java 原生 Agent OS",
        subcommands = {
                OryxOsCli.Init.class,
                OryxOsCli.Chat.class,
                OryxOsCli.Serve.class
        })
public class OryxOsCli implements Runnable {

    public static final String VERSION = "1.0.0-SNAPSHOT";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new OryxOsCli()).execute(args);
        System.exit(exitCode);
    }

    /** 无参运行：打印版本横幅 + 用法提示。 */
    @Override
    public void run() {
        System.out.println(banner());
    }

    static String banner() {
        return """
                OryxOS %s
                企业级 Java 原生 Agent OS —— 私有、可控、可审计的智能体统一底座
                JDK %s · %s
                用 `oryxos --help` 查看命令，`oryxos --version` 查看版本。\
                """.formatted(
                VERSION,
                System.getProperty("java.version"),
                "https://github.com/oryxos-jin/oryxos");
    }

    /** 供 `oryxos --version` 使用（技术方案 §8.7）。 */
    static class VersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() {
            return new String[] {
                    "OryxOS " + VERSION,
                    "企业级 Java 原生 Agent OS",
                    "JDK " + System.getProperty("java.version")
            };
        }
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
