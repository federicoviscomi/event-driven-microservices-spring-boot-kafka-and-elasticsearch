package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.StringJoiner;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {
    @Override
    public String toString() {
        return new StringJoiner("\n\t", RetryConfigData.class.getSimpleName() + "[\n\t", "]")
                .add("initialIntervalMs=" + initialIntervalMs)
                .add("maxIntervalMs=" + maxIntervalMs)
                .add("multiplier=" + multiplier)
                .add("maxAttempts=" + maxAttempts)
                .add("sleepTimeMs=" + sleepTimeMs)
                .toString();
    }

    private Long initialIntervalMs;
    private Long maxIntervalMs;
    private Double multiplier;
    private Integer maxAttempts;
    private Long sleepTimeMs;
}
