package com.software.craft.lille.train_kata.api.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.util.List;

public record Ticket(
    @JsonProperty("train_id") String train,
    @JsonProperty("booking_reference") String bookingReference,
    @JsonProperty("seats") List<String> seats) {
  public static Ticket from(Reservation reservation) {
    return new Ticket(
        reservation.trainId(),
        reservation.bookingId(),
        reservation.seats().stream().map(Seat::formatted).toList());
  }
}
