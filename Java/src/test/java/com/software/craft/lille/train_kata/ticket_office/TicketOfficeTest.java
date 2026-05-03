package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.model.DataForTrain;
import com.software.craft.lille.train_kata.api.model.DataForTrain.TrainSeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TicketOfficeTest {
    @InjectMocks
    private TicketOffice ticketOffice;
    @Mock
    private TrainService trainService;

    @BeforeEach
    void setUp() {
        Mockito.reset(trainService);
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoachInAnEmptyTrain() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        given(trainService.dataForTrain("requestedTrain")).willReturn(
                new DataForTrain(Map.of(
                        "1A", new TrainSeat("A", 1, ""),
                        "2A", new TrainSeat("A", 2, "")
                ))
        );
        final Reservation succeededReservation = new Reservation(reservationRequest.trainId(),
                List.of(new Seat("A", 1), new Seat("A", 2)), "a booking reference");
        given(trainService.reserveSeatsOnTrain(anyString(), anyList())).willReturn(succeededReservation);

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isEqualTo(succeededReservation);
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_inACoachWhereTheRequestedNumberOfSeatsIsAvailable() {
        final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 2);
        given(trainService.dataForTrain("requestedTrain")).willReturn(
                new DataForTrain(Map.of(
                        "1A", new TrainSeat("A", 1, "another booking reference"),
                        "2A", new TrainSeat("A", 2, ""),
                        "3A", new TrainSeat("A", 3, "another booking reference"),
                        "1B", new TrainSeat("B", 1, ""),
                        "2B", new TrainSeat("B", 2, "")
                ))
        );
        final Reservation succeededReservation = new Reservation(reservationRequest.trainId(),
                List.of(new Seat("B", 1), new Seat("B", 2)), "a booking reference");
        given(trainService.reserveSeatsOnTrain(anyString(), anyList())).willReturn(succeededReservation);

        final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation).isEqualTo(succeededReservation);
    }
}