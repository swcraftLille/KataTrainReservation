package com.software.craft.lille.train_kata.api.external.properties;

import static java.util.function.Predicate.not;

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booking-reference-service")
public record BookingReferenceServiceProperties(String url) implements HttpClientProperties {
  public BookingReferenceServiceProperties {
    Optional.ofNullable(url)
        .filter(not(String::isBlank))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "booking-reference-service.url is required. Current value is '%s'"
                        .formatted(url)));
  }
}
