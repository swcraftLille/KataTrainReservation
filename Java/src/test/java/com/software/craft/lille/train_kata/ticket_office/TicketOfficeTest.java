package com.software.craft.lille.train_kata.ticket_office;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketOfficeTest {

    @Test
    public void makeReservation_affectBookingReferenceToTheReservation_fromBookingReferenceService() {
        TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference");

        Reservation reservation = ticketOffice.makeReservation(new ReservationRequest("1", 1));

        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isEqualTo("new booking reference");
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoach() {
        TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference");

        Reservation reservation = ticketOffice.makeReservation(new ReservationRequest("requestedTrain", 2));

        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo("requestedTrain");
        assertThat(reservation.seats()).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(
                        new Seat("a coach", 1),
                        new Seat("a coach", 2)
                );
    }
}