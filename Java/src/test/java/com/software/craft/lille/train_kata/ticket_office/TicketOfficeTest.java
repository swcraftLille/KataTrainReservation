package com.software.craft.lille.train_kata.ticket_office;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketOfficeTest {
    @InjectMocks
    private TicketOffice ticketOffice;
    @Mock
    private TrainService trainService;

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoach() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        final Reservation succeededReservation = new Reservation(reservationRequest.trainId(),
                List.of(new Seat("A", 1), new Seat("A", 2)), "a booking reference");
        given(trainService.reserveSeatsOnTrain(anyString(), anyList())).willReturn(succeededReservation);

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isEqualTo(succeededReservation);
        verify(trainService).reserveSeatsOnTrain("requestedTrain", List.of(new Seat("A", 1), new Seat("A", 2)));
    }
}