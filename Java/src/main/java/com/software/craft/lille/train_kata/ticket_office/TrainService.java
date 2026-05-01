package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
public class TrainService {
    private final TrainDataServiceClient trainDataServiceClient;
    private final BookingReferenceClient bookingReferenceClient;
    private final JsonMapper jsonMapper;

    public TrainService(TrainDataServiceClient trainDataServiceClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataServiceClient = trainDataServiceClient;
        this.bookingReferenceClient = bookingReferenceClient;
        jsonMapper = new JsonMapper();
    }

    public DataForTrain reserveSeatsOnTrain(String trainId, List<Seat> seats) {
        final String seatsToBook = jsonMapper.writeValueAsString(seats.stream()
                .map(seat -> "%d%s".formatted(seat.seatNumber(), seat.coach()))
                .toList());
        final ResponseEntity<String> response = trainDataServiceClient.reserveSeats(
                trainId,
                seatsToBook,
                bookingReferenceClient.getBookingReference());
        return jsonMapper.readValue(response.getBody(), DataForTrain.class);
    }
}
