package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TicketOfficeTest {
    private final BookingReferenceClient bookingReferenceClient = () -> "new booking reference";


    @Test
    public void makeReservation_affectBookingReferenceToTheReservation_fromBookingReferenceService() {
        final ReservationRequest reservationRequest = new ReservationRequest("1", 1);
        final TicketOffice ticketOffice = new TicketOffice(bookingReferenceClient,
                new StubTrainService(reservationRequest));

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isEqualTo("new booking reference");
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoach() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        final TicketOffice ticketOffice = new TicketOffice(bookingReferenceClient, new StubTrainService(reservationRequest));

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo("requestedTrain");
        assertThat(reservation.seats()).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(
                        new Seat("A", 1),
                        new Seat("A", 2)
                );
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrainOnTheSameCoach_usingTrainService() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        final TicketOffice ticketOffice = new TicketOffice(bookingReferenceClient,
                new StubTrainService(reservationRequest));

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo("requestedTrain");
        assertThat(reservation.seats()).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(
                        new Seat("A", 1),
                        new Seat("A", 2)
                );
    }
}