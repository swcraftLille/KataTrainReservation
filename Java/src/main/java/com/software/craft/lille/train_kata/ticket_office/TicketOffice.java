package com.software.craft.lille.train_kata.ticket_office;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketOffice {
    public Reservation makeReservation(ReservationRequest reservationRequest) {
        // Implements me
        return new Reservation("", List.of(), "booking reference");
    }
}
