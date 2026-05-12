package com.software.craft.lille.train_kata.ticket_office.reservation;

import java.util.List;

public record Reservation(String trainId, List<Seat> seats, String bookingId) {
}
