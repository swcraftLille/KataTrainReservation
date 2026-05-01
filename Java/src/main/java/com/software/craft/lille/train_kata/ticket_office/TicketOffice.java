package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class TicketOffice {
    private final BookingReferenceClient bookingReferenceClient;
    private final TrainDataServiceClient trainService;
    private final JsonMapper jsonMapper = new JsonMapper();

    public TicketOffice(BookingReferenceClient bookingReferenceClient, TrainDataServiceClient trainDataServiceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
        this.trainService = trainDataServiceClient;
    }

    public Reservation makeReservation(ReservationRequest reservationRequest) {
        final String bookingReference = bookingReferenceClient.getBookingReference();
        return sendReservationRequest(reservationRequest, bookingReference);
    }

    private Reservation sendReservationRequest(ReservationRequest reservationRequest, String bookingReference) {
        final ResponseEntity<String> response = trainService.reserveSeats(reservationRequest.trainId(), "{}", bookingReference);
        final DataForTrain dataForTrain = jsonMapper.readValue(response.getBody(), DataForTrain.class);
        return new Reservation(reservationRequest.trainId(), dataForTrain.seatsBookedWithReference(bookingReference),
                bookingReference);
    }

}
