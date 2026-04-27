package br.com.esdevcode.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "csvChunkExecutor")
    public Executor csvChunkExecutor(ImportProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.threadPoolSize());
        executor.setMaxPoolSize(properties.threadPoolSize());
        executor.setQueueCapacity(properties.threadPoolSize() * 8);
        executor.setThreadNamePrefix("csv-chunk-");
        executor.initialize();
        return executor;
    }
}
