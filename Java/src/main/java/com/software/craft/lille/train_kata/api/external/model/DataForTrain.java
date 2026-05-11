package com.software.craft.lille.train_kata.api.external.model;

import static java.util.function.Predicate.not;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DataForTrain(@JsonProperty("seats") Map<String, TrainSeat> seats)
    implements Serializable {
  public static DataForTrain empty() {
    return new DataForTrain(Map.of());
  }

  public int totalNumberOfSeats() {
    return seats().size();
  }

  public long numberOfBookedSeats() {
    return seats().values().stream().filter(not(TrainSeat::isAvailable)).distinct().count();
  }

  public List<Seat> availableSeatsForCoach(String coach) {
    return seats().values().stream()
        .filter(seat -> seat.coach().equalsIgnoreCase(coach))
        .filter(TrainSeat::isAvailable)
        .map(TrainSeat::toSeat)
        .toList();
  }

  public record TrainSeat(
      @JsonProperty("coach") String coach,
      @JsonProperty("seat_number") int seatNumber,
      @JsonProperty("booking_reference") String bookingReference)
      implements Serializable {
    public Seat toSeat() {
      return new Seat(coach(), seatNumber());
    }

    public boolean isAvailable() {
      return Optional.ofNullable(bookingReference).orElse("").isBlank();
    }
  }
}
