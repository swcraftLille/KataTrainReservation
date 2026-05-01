package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketOffice {
    private final BookingReferenceClient bookingReferenceClient;

    public TicketOffice(BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;

    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        // Implements me
        return new Reservation("", List.of(), bookingReferenceClient.getBookingReference());
    }
}
