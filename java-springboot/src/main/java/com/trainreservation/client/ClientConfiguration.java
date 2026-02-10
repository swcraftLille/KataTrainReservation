package com.trainreservation.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {

    @Bean
    public BookingReferenceClient bookingReferenceClient(
            @Value("${booking-reference-service.url}") String baseUrl) {
        RestClient restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();
        return new BookingReferenceClient(restClient);
    }

    @Bean
    public TrainDataClient trainDataClient(
            @Value("${train-data-service.url}") String baseUrl) {
        RestClient restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .build();
        return new TrainDataClient(restClient);
    }
}
