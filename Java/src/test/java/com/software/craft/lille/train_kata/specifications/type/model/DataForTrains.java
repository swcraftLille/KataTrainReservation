package com.software.craft.lille.train_kata.specifications.type.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public record DataForTrains(Map<String, TrainSeat> seats) {
    @JsonCreator
    public DataForTrains(@JsonProperty("seats") Map<String, TrainSeat> seats) {
        this.seats = Optional.ofNullable(seats).orElse(Map.of());
    }

    public Set<TrainSeat> seatsWithBookingReference(String reference) {
        String bookingReference = Optional.ofNullable(reference).orElse("");
        return seats.values()
                .stream()
                .filter(seat -> bookingReference.equalsIgnoreCase(seat.bookingReference()))
                .collect(Collectors.toSet());
    }

    public String print() {
        final StringJoiner train = new StringJoiner(System.lineSeparator());
        seats().values()
                .stream()
                .collect(groupingBy(TrainSeat::coach))
                .forEach((coach, seats) -> train.add("%s: %s".formatted(coach, printSeats(seats))));
        return train.toString();
    }

    private String printSeats(List<TrainSeat> seats) {
        return seats.stream()
                .map(seat -> String.format("[%s]", seat.hasBookingReference() ? "X" : "O"))
                .reduce("", "%s%s"::formatted);
    }
}
