package com.software.craft.lille.train_kata.ticket_office;

import static java.util.Map.Entry.comparingByValue;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.teeing;

import com.software.craft.lille.train_kata.api.model.DataForTrain;
import com.software.craft.lille.train_kata.api.model.DataForTrain.TrainSeat;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import com.software.craft.lille.train_kata.ticket_office.reservation.SeatCapacity;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TicketOffice {
  private final TrainService trainService;

  public TicketOffice(TrainService trainService) {
    this.trainService = trainService;
  }

  public Reservation makeReservation(final ReservationRequest reservationRequest) {
    final DataForTrain dataForTrain = trainService.dataForTrain(reservationRequest.trainId());
    final SeatCapacity seatCapacity =
        new SeatCapacity(
            reservationRequest.seatCount(),
            dataForTrain.numberOfBookedSeats(),
            dataForTrain.totalNumberOfSeats());
    if (seatCapacity.reachReservationThreshold()) {
      return new Reservation(reservationRequest.trainId(), List.of(), "");
    }
    return trainService.reserveSeatsOnTrain(
        reservationRequest.trainId(),
        chooseSeatsToBook(dataForTrain, reservationRequest.seatCount()));
  }

  private List<Seat> chooseSeatsToBook(
      final DataForTrain dataForTrain, final int requestedNumberOfSeats) {
    final Map<String, SeatCapacity> seatCapacityByCoach =
        dataForTrain.seats().values().stream()
            .collect(
                groupingBy(
                    TrainSeat::coach,
                    teeing(
                        filtering(not(TrainSeat::isAvailable), counting()),
                        counting(),
                        (bookedSeatsInCoach, totalSeatInCoach) ->
                            new SeatCapacity(
                                requestedNumberOfSeats, bookedSeatsInCoach, totalSeatInCoach))));
    return seatCapacityByCoach.entrySet().stream()
        .filter(entry -> entry.getValue().hasAvailableRequestedSeatsNumber())
        .min(comparingByValue(SeatCapacity::higherCapacityAfterReservation))
        .map(entry -> dataForTrain.availableSeatsForCoach(entry.getKey()))
        .orElse(List.of())
        .subList(0, requestedNumberOfSeats);
  }
}
