package com.software.craft.lille.train_kata.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

import static java.util.function.Predicate.not;

@ConfigurationProperties(prefix = "train-data-service")
public record TrainDataServiceProperties(String url) implements HttpClientProperties {
    public TrainDataServiceProperties {
        Optional.ofNullable(url)
                .filter(not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("train-data-service.url is required. Current value is '%s'".formatted(url)));
    }
}
