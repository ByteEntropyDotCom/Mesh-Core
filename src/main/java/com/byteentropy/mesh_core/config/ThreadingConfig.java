package com.byteentropy.mesh_core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class ThreadingConfig {

    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        // Correct Java 21 syntax: Thread.ofVirtual().factory()
        ThreadFactory factory = Thread.ofVirtual().name("mesh-vt-", 0).factory();
        return new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(factory));
    }

    @Bean
    public AsyncTaskExecutor virtualThreadExecutor() {
        // Shorthand for the same behavior
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}