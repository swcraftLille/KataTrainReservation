package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
public class TicketOffice {
    private final BookingReferenceClient bookingReferenceClient;
    private final TrainDataServiceClient trainService;

    public TicketOffice(BookingReferenceClient bookingReferenceClient, TrainDataServiceClient trainDataServiceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
        this.trainService = trainDataServiceClient;
    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        String bookingReference = bookingReferenceClient.getBookingReference();
        ResponseEntity<String> response = trainService.reserveSeats(reservationRequest.trainId(), "{}", bookingReference);
        DataForTrain dataForTrain = new JsonMapper().readValue(response.getBody(), DataForTrain.class);
        return new Reservation(reservationRequest.trainId(),
                dataForTrain.seats()
                        .values()
                        .stream()
                        .filter(this::hasBeenBooked)
                        .map(this::toSeat)
                        .toList(),
                bookingReference);
    }

    private Seat toSeat(DataForTrain.Seat value) {
        return new Seat(value.coach(), value.seatNumber());
    }

    private boolean hasBeenBooked(DataForTrain.Seat value) {
        return Optional.ofNullable(value)
                .map(DataForTrain.Seat::bookingReference)
                .filter(not(String::isBlank))
                .isPresent();
    }
}
