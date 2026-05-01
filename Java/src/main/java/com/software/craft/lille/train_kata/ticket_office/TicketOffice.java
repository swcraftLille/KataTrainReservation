package com.software.craft.lille.train_kata.ticket_office;

import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class TicketOffice {
    private final TrainService trainService;

    public TicketOffice(TrainService trainService) {
        this.trainService = trainService;
    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        return trainService.reserveSeatsOnTrain(reservationRequest.trainId(),
                IntStream.rangeClosed(1, reservationRequest.seatCount())
                        .mapToObj(seatNumber -> new Seat("A", seatNumber))
                        .toList());
    }
}
