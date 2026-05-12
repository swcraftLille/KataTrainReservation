package com.software.craft.lille.train_kata.specifications;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;

public class TestContainerConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Container
    static GenericContainer<?> bookingReference =
            new GenericContainer<>("docker-booking-reference:latest")
                    .withExposedPorts(8082)
                    .withStartupTimeout(Duration.ofSeconds(30));
    @Container
    static GenericContainer<?> trainData =
            new GenericContainer<>("docker-train-data:latest")
                    .withExposedPorts(8081)
                    .withStartupTimeout(Duration.ofSeconds(30))
                    .dependsOn(bookingReference);

    static {
        System.setProperty("testcontainers.reuse.enable", "true");
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        bookingReference.start();
        trainData.start();
        System.setProperty("BOOKING_REFERENCE_HOST_URL", "http://%s".formatted(bookingReference.getHost()));
        System.setProperty("BOOKING_REFERENCE_PORT", bookingReference.getMappedPort(8082).toString());
        System.setProperty("TRAIN_DATA_SERVICE_HOST_URL", "http://%s".formatted(trainData.getHost()));
        System.setProperty("TRAIN_DATA_SERVICE_PORT", trainData.getMappedPort(8081).toString());
    }
}
