package com.software.craft.lille.train_kata.ticket_office;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.software.craft.lille.train_kata.DataForTrainFixtures;
import com.software.craft.lille.train_kata.api.model.DataForTrain.TrainSeat;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketOfficeTest {
  @InjectMocks private TicketOffice ticketOffice;
  @Mock private TrainService trainService;

  @BeforeEach
  void setUp() {
    Mockito.reset(trainService);
  }

  @Test
  public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoachInAnEmptyTrain() {
    final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 1);
    final List<TrainSeat> trainSeats =
        List.of(new TrainSeat("A", 1, ""), new TrainSeat("A", 2, ""));
    given(trainService.dataForTrain("requestedTrain"))
        .willReturn(DataForTrainFixtures.createTrain(trainSeats).data());
    final Reservation succeededReservation =
        new Reservation(
            reservationRequest.trainId(), List.of(new Seat("A", 1)), "a booking reference");
    given(trainService.reserveSeatsOnTrain(anyString(), anyList()))
        .willReturn(succeededReservation);

    final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

    assertThat(reservation).isEqualTo(succeededReservation);
  }

  @Test
  public void
      makeReservation_bookTheRequestedNumberOfSeatsOnATrain_inACoachWhereTheRequestedNumberOfSeatsIsAvailable() {
    final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 1);
    final List<TrainSeat> trainSeats =
        List.of(
            new TrainSeat("A", 1, "another booking reference"),
            new TrainSeat("A", 2, ""),
            new TrainSeat("A", 3, "another booking reference"),
            new TrainSeat("B", 1, ""),
            new TrainSeat("B", 2, ""));
    given(trainService.dataForTrain("requestedTrain"))
        .willReturn(DataForTrainFixtures.createTrain(trainSeats).addSeats(trainSeats).data());
    final Reservation succeededReservation =
        new Reservation(
            reservationRequest.trainId(), List.of(new Seat("B", 1)), "a booking reference");
    given(trainService.reserveSeatsOnTrain(anyString(), anyList()))
        .willReturn(succeededReservation);

    final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

    assertThat(reservation).isEqualTo(succeededReservation);
  }

  @Test
  public void
      makeReservation_doNotBookSeat_whenTheRequestedTrainHasReached70PercentOfItsBookingCapacity() {
    final ReservationRequest reservationRequest = new ReservationRequest("requestedTrain", 1);
    final List<TrainSeat> trainSeats =
        List.of(
            new TrainSeat("A", 1, "booked"),
            new TrainSeat("A", 2, "booked"),
            new TrainSeat("A", 3, "booked"),
            new TrainSeat("A", 4, ""),
            new TrainSeat("B", 1, "booked"),
            new TrainSeat("B", 2, "booked"),
            new TrainSeat("B", 3, "booked"),
            new TrainSeat("B", 4, "booked"),
            new TrainSeat("B", 5, ""),
            new TrainSeat("C", 1, "booked"),
            new TrainSeat("C", 2, "booked"),
            new TrainSeat("C", 3, ""),
            new TrainSeat("C", 4, "booked"),
            new TrainSeat("C", 5, ""),
            new TrainSeat("C", 6, ""),
            new TrainSeat("C", 7, "booked"),
            new TrainSeat("C", 8, "booked"));
    given(trainService.dataForTrain("requestedTrain"))
        .willReturn(DataForTrainFixtures.createTrain(trainSeats).addSeats(trainSeats).data());

    final Reservation reservation = ticketOffice.makeReservation(reservationRequest);

    assertThat(reservation).isEqualTo(new Reservation(reservationRequest.trainId(), List.of(), ""));
  }
}
