package com.software.craft.lille.train_kata.ticket_office;

import static java.util.function.Predicate.not;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TicketOffice {
    private final TrainService trainService;

    public TicketOffice(TrainService trainService) {
        this.trainService = trainService;
    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        return trainService.reserveSeatsOnTrain(reservationRequest.trainId(),
                defineSeatsToBook(reservationRequest));
    }

    private List<Seat> defineSeatsToBook(ReservationRequest reservationRequest) {
        final Map<String, List<Seat>> coachWithNumberOfSeatsAvailable =
                trainService.dataForTrain(reservationRequest.trainId())
                        .coachWithNumberOfSeatsAvailable(reservationRequest.seatCount());
        return coachWithNumberOfSeatsAvailable.values()
                .stream()
                .filter(not(List::isEmpty))
                .findAny()
                .orElseGet(Collections::emptyList).subList(0, reservationRequest.seatCount());
    }
}
