package com.pablogb.psychologger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "transcriptionExecutor")
    public Executor transcriptionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // 2 threads always ready
        executor.setMaxPoolSize(5);       // up to 5 concurrent transcriptions
        executor.setQueueCapacity(10);    // queue up to 10 more
        executor.setThreadNamePrefix("transcription-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "auditExecutor")
    public Executor auditExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("audit-");
        executor.initialize();
        // this is the key — wraps executor to propagate security context
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
