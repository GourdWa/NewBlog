package com.hzx.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Zixiang Hu
 * @description 异步配置
 * @create 2022-03-02-10:30
 */
@Configuration
@EnableAsync
public class ExecutorConfig {
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1024);
        executor.setThreadNamePrefix("asyncExecutor");
        executor.initialize();
        return executor;
    }
}
