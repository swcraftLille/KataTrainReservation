package com.software.craft.lille.train_kata.configuration.properties;

public interface HttpClientProperties {
    String url();
    String port();
    default String baseUrl() {
        return "%s:%s".formatted(url(), port());
    }
}
