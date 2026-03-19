package com.software.craft.lille.train_kata.specifications.type.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record TrainCoach(Designation designation, int numberOfSeats) {

    public Collection<String> toSeats() {
        return IntStream.rangeClosed(1, numberOfSeats())
                .mapToObj(seatNumber -> SeatNumber.from(designation, seatNumber).value())
                .collect(Collectors.toSet());
    }

    public record Designation(String value) {
    }

    public record SeatNumber(String value) {
        public static SeatNumber from(Designation designation, int seatNumber) {
            return new SeatNumber("%s%s".formatted(seatNumber, designation.value()));
        }
    }

    public record SeatNumbers(Collection<SeatNumber> elements) {
        public Set<String> values() {
            return elements().stream()
                    .map(SeatNumber::value)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }
}
