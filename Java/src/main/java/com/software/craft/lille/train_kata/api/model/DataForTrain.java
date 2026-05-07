package com.software.craft.lille.train_kata.api.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.craft.lille.train_kata.ticket_office.Seat;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DataForTrain(@JsonProperty("seats") Map<String, TrainSeat> seats) implements Serializable {
    public static DataForTrain empty() {
        return new DataForTrain(Map.of());
    }

    public Map<String, List<Seat>> coachWithNumberOfSeatsAvailable(int numberOfSeats) {
        return seats()
                .values()
                .stream()
                .filter(TrainSeat::isAvailable)
                .map(TrainSeat::toSeat)
                .collect(groupingBy(Seat::coach))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= numberOfSeats)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public record TrainSeat(@JsonProperty("coach") String coach,
                            @JsonProperty("seat_number") int seatNumber,
                            @JsonProperty("booking_reference") String bookingReference) implements Serializable {
        public Seat toSeat() {
            return new Seat(coach(), seatNumber());
        }

        public boolean isAvailable() {
            return Optional.ofNullable(bookingReference).orElse("").isBlank();
        }
    }
}
