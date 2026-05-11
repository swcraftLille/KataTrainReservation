package com.software.craft.lille.train_kata.ticket_office;

import static java.lang.Double.compare;
import static java.lang.Long.compare;
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
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TicketOffice {
  private static final double MAXIMUM_RESERVATION_THRESHOLD = 0.7d;
  private final TrainService trainService;

  public TicketOffice(TrainService trainService) {
    this.trainService = trainService;
  }

  public Reservation makeReservation(ReservationRequest reservationRequest) {
    final DataForTrain dataForTrain = trainService.dataForTrain(reservationRequest.trainId());
    if (trainReachReservationThreshold(dataForTrain, reservationRequest)) {
      return new Reservation(reservationRequest.trainId(), List.of(), "");
    }
    return trainService.reserveSeatsOnTrain(
        reservationRequest.trainId(), defineSeatsToBook(reservationRequest, dataForTrain));
  }

  private boolean trainReachReservationThreshold(
      DataForTrain dataForTrain, ReservationRequest reservationRequest) {
    final int totalTrainSeats = dataForTrain.totalNumberOfSeats();
    final Map<String, TrainSeat> seats = dataForTrain.seats();
    final long bookedSeatsNumber =
        seats.values().stream().filter(not(TrainSeat::isAvailable)).distinct().count();
    final CoachAvailability coachAvailability =
        new CoachAvailability(reservationRequest.seatCount(), bookedSeatsNumber, totalTrainSeats);
    return coachAvailability.availabilityThresholdAfterReservation()
        >= MAXIMUM_RESERVATION_THRESHOLD;
  }

  private List<Seat> defineSeatsToBook(
      ReservationRequest reservationRequest, DataForTrain dataForTrain) {
    final Map<String, TrainSeat> seats = dataForTrain.seats();
    final Map<String, CoachAvailability> seatsAvailabilityByCoach =
        seats.values().stream()
            .collect(
                groupingBy(
                    TrainSeat::coach,
                    teeing(
                        filtering(not(TrainSeat::isAvailable), counting()),
                        counting(),
                        (bookedSeats, totalSeat) ->
                            new CoachAvailability(
                                reservationRequest.seatCount(), bookedSeats, totalSeat))));
    return seatsAvailabilityByCoach.entrySet().stream()
        .filter(entry -> entry.getValue().hasAvailableRequestedSeatsNumber())
        .min(comparingByValue(this::preferringCoachWithHigherReservationCapacity))
        .map(entry -> availableSeatsForCoach(entry.getKey(), dataForTrain))
        .orElse(List.of())
        .subList(0, reservationRequest.seatCount());
  }

  private List<Seat> availableSeatsForCoach(String coach, DataForTrain dataForTrain) {
    return dataForTrain.seats().values().stream()
        .filter(seat -> seat.coach().equalsIgnoreCase(coach))
        .filter(TrainSeat::isAvailable)
        .map(TrainSeat::toSeat)
        .toList();
  }

  private int preferringCoachWithHigherReservationCapacity(
      CoachAvailability first, CoachAvailability next) {
    double firstCoachReservationThreshold = first.availabilityThresholdAfterReservation();
    double nextCoachReservationThreshold = next.availabilityThresholdAfterReservation();
    boolean firstCoachReachMaxCapacity =
        firstCoachReservationThreshold > MAXIMUM_RESERVATION_THRESHOLD;
    boolean nextCoachReachMaxCapacity =
        nextCoachReservationThreshold > MAXIMUM_RESERVATION_THRESHOLD;
    if (firstCoachReachMaxCapacity != nextCoachReachMaxCapacity) {
      return firstCoachReachMaxCapacity ? -1 : 1;
    }
    int capacityComparison = compare(firstCoachReservationThreshold, nextCoachReservationThreshold);
    if (capacityComparison == 0) {
      return compare(first.totalSeats(), next.totalSeats());
    }
    return capacityComparison;
  }

  public record CoachAvailability(long requestedSeats, long bookedSeats, long totalSeats) {
    public double availabilityThresholdAfterReservation() {
      return (double) (requestedSeats + bookedSeats) / totalSeats;
    }

    public boolean hasAvailableRequestedSeatsNumber() {
      return (totalSeats - bookedSeats) >= requestedSeats;
    }
  }
}
