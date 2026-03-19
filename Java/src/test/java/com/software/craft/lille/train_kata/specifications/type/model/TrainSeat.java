package com.software.craft.lille.train_kata.specifications.type.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

import static java.util.function.Predicate.not;

public record TrainSeat(
        @JsonProperty("booking_reference")
        String bookingReference,
        @JsonProperty("seat_number")
        String seatNumber,
        @JsonProperty("coach")
        String coach) {
    public boolean hasBookingReference() {
        return Optional.ofNullable(bookingReference)
                .filter(not(String::isBlank))
                .isPresent();
    }
}
