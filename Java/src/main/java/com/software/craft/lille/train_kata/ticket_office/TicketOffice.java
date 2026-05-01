package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class TicketOffice {
    private final BookingReferenceClient bookingReferenceClient;

    public TicketOffice(BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;

    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        return new Reservation(reservationRequest.trainId(),
                IntStream.rangeClosed(1, reservationRequest.seatCount())
                        .mapToObj(seatNumber -> new Seat("a coach", seatNumber))
                        .toList(),
                bookingReferenceClient.getBookingReference());
    }
}
