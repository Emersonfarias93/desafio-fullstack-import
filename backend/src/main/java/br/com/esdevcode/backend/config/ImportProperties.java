package br.com.esdevcode.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.import")
public record ImportProperties(
        String uploadDir,
        int chunkSize,
        int threadPoolSize
) {
    public ImportProperties {
        if (uploadDir == null || uploadDir.isBlank()) {
            uploadDir = "uploads";
        }
        if (chunkSize <= 0) {
            chunkSize = 10000;
        }
        if (threadPoolSize <= 0) {
            threadPoolSize = 50;
        }
    }
}
