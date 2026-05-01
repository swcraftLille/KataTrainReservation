package com.software.craft.lille.train_kata.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.craft.lille.train_kata.ticket_office.Seat;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DataForTrain(@JsonProperty("seats") Map<String, TrainSeat> seats) implements Serializable {
    public List<Seat> seatsBookedWithReference(String bookingReference) {
        return seats()
                .values()
                .stream()
                .filter(trainSeat -> trainSeat.hasBookingReference(bookingReference))
                .map(TrainSeat::toSeat)
                .toList();
    }

    public record TrainSeat(@JsonProperty("coach") String coach,
                            @JsonProperty("seat_number") int seatNumber,
                            @JsonProperty("booking_reference") String bookingReference) implements Serializable {
        public Seat toSeat() {
            return new Seat(coach(), seatNumber());
        }

        public boolean hasBookingReference(String bookingReference) {
            final String reference = Optional.ofNullable(bookingReference).orElse("");
            final String seatBookingReference = Optional.ofNullable(bookingReference()).orElse("");
            return seatBookingReference.equalsIgnoreCase(reference);
        }
    }
}
