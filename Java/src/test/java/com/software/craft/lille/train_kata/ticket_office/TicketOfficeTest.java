package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TicketOfficeTest {
    @InjectMocks
    private TicketOffice ticketOffice;
    @Mock
    private BookingReferenceClient bookingReferenceClient;
    @Mock
    private TrainDataServiceClient trainDataServiceClient;

    @Test
    public void makeReservation_affectBookingReferenceToTheReservation_fromBookingReferenceService() {
        given(bookingReferenceClient.getBookingReference())
                .willReturn("new booking reference");
        given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString()))
                .willReturn(ResponseEntity.ok("""
                        { "seats": {} }"""));

        final ReservationRequest reservationRequest = new ReservationRequest("1", 1);

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isEqualTo("new booking reference");
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoach() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        final TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference", new StubTrainService(reservationRequest));

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
        final TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference",
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