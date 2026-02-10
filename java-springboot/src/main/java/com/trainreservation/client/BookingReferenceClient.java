package com.trainreservation.client;

import org.springframework.web.client.RestClient;

public class BookingReferenceClient {

    private final RestClient restClient;

    public BookingReferenceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getBookingReference() {
        return restClient.get()
            .uri("/booking_reference")
            .retrieve()
            .body(String.class);
    }
}
