package com.software.craft.lille.train_kata.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

public record DataForTrain(@JsonProperty("seats") Map<String, Seat> seats) implements Serializable {
    public record Seat(@JsonProperty("coach") String coach,
                       @JsonProperty("seat_number") int seatNumber,
                       @JsonProperty("booking_reference") String bookingReference) implements Serializable {
    }
}
