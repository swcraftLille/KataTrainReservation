package com.trainreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SeatDTO(
    @JsonProperty("booking_reference") String bookingReference,
    @JsonProperty("seat_number") String seatNumber,
    String coach
) {}
