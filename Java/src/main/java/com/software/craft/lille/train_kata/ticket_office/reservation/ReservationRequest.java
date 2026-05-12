package com.software.craft.lille.train_kata.ticket_office.reservation;

import static java.util.function.Predicate.not;

import com.software.craft.lille.train_kata.ticket_office.exception.UnusableData;
import java.util.Optional;

public record ReservationRequest(String trainId, Integer seatCount) {
  public ReservationRequest {
    Optional.ofNullable(trainId)
        .filter(not(String::isBlank))
        .orElseThrow(
            () ->
                new UnusableData(
                    "A train id is required to make a reservation. Current value '%s'"
                        .formatted(trainId)));
    Optional.ofNullable(seatCount)
        .filter(value -> value > 0)
        .orElseThrow(
            () ->
                new UnusableData(
                    "A positive number of seat to book is required to make a reservation. Current value '%s'"
                        .formatted(seatCount)));
  }
}
