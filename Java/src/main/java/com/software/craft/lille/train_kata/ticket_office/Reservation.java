package com.software.craft.lille.train_kata.ticket_office;

import java.util.List;

public record Reservation(String trainId, List<Seat> seats, String bookingId) {
  public static Reservation empty() {
    return new Reservation("", List.of(), "");
  }
}
