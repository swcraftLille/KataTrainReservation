package com.software.craft.lille.train_kata.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

import static java.util.function.Predicate.not;

@ConfigurationProperties(prefix = "booking-reference-service")
public record BookingReferenceServiceProperties(String url) implements HttpClientProperties {
    public BookingReferenceServiceProperties {
        Optional.ofNullable(url)
                .filter(not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("booking-reference-service.url is required. Current value is '%s'".formatted(url)));
    }
}
