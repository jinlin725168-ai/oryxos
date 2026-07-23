package com.oryxos.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * OryxOS 启动主类（技术方案 §10）。扫描 com.oryxos 下全部模块的 Spring Bean。
 * <p>骨架阶段：暂时排除数据源/JPA 自动配置，避免在没有配置 SQLite 时启动失败；
 * US-5 接入 oryxos-storage 时移除该排除并提供 application.yaml 数据源配置。
 */
@SpringBootApplication(
        scanBasePackages = "com.oryxos",
        exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class OryxOsApplication {

    public static void main(String[] args) {
        // TODO：CLI 命令（Picocli）与 Spring 上下文的分派——需要 LLM 的命令启动上下文，
        //       不需要的直接走文件操作（技术方案 §8.7）。骨架先启动 Spring 上下文。
        org.springframework.boot.SpringApplication.run(OryxOsApplication.class, args);
    }
}
