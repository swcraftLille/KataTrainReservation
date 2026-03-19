package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.configuration.api.booking_reference.BookingReferenceClient;
import com.software.craft.lille.train_kata.configuration.api.train_data_service.TrainDataServiceClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
public class TicketOffice {
    private final BookingReferenceClient bookingReferenceClient;
    private final TrainDataServiceClient trainDataServiceClient;
    private final JsonMapper jsonMapper;

    public TicketOffice(TrainDataServiceClient trainDataServiceClient, BookingReferenceClient bookingReferenceClient, JsonMapper jsonMapper) {
        this.trainDataServiceClient = trainDataServiceClient;
        this.bookingReferenceClient = bookingReferenceClient;
        this.jsonMapper = jsonMapper;
    }

    public Reservation makeReservation(ReservationRequest request) {
        String bookingReference = bookingReferenceClient.getBookingReference();
        String seatsToReserve = jsonMapper.writeValueAsString(List.of("1A", "2A", "3A", "4A"));
        trainDataServiceClient.reserveSeats(request.trainId, seatsToReserve, bookingReference);
        return new Reservation(request.trainId,
                List.of(new Seat("A", 1),
                        new Seat("A", 2),
                        new Seat("A", 3),
                        new Seat("A", 4)),
                bookingReference);
    }

}