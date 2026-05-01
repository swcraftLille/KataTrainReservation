package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static org.springframework.http.HttpStatus.OK;

@Service
public class TrainService {
    private static final Logger logger = LoggerFactory.getLogger(TrainService.class);
    private final TrainDataServiceClient trainDataServiceClient;
    private final BookingReferenceClient bookingReferenceClient;
    private final JsonMapper jsonMapper;

    public TrainService(TrainDataServiceClient trainDataServiceClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataServiceClient = trainDataServiceClient;
        this.bookingReferenceClient = bookingReferenceClient;
        jsonMapper = new JsonMapper();
    }

    public Reservation reserveSeatsOnTrain(String trainId, List<Seat> seats) {
        final String bookingReference = bookingReferenceClient.getBookingReference();
        if (seatsHasBeenBooked(trainId, seats, bookingReference)) {
            return new Reservation(trainId, seats, bookingReference);
        }
        return new Reservation(trainId, seats, "");
    }

    private boolean seatsHasBeenBooked(String trainId, List<Seat> seats, String bookingReference) {
        final String seatsToBook = jsonMapper.writeValueAsString(seats.stream()
                .map(seat -> "%d%s".formatted(seat.seatNumber(), seat.coach()))
                .toList());
        return bookSeats(trainId, seatsToBook, bookingReference).isPresent();
    }

    private Optional<DataForTrain> bookSeats(String trainId, String seatsToBook, String bookingReference) {
        Optional<DataForTrain> dataForTrain = Optional.empty();
        try {
            dataForTrain = Optional.ofNullable(trainDataServiceClient.reserveSeats(
                            trainId,
                            seatsToBook,
                            bookingReference))
                    .filter(this::reservationSucceed)
                    .map(ResponseEntity::getBody)
                    .map(this::readDataForTrain);
        } catch (JacksonException jacksonException) {
            logger.warn("[{}] Impossible to book seats '{}' for train '{}'",
                    getClass().getSimpleName(),
                    seatsToBook,
                    trainId,
                    jacksonException);
        }
        return dataForTrain;
    }

    private DataForTrain readDataForTrain(String body) {
        return jsonMapper.readValue(body, DataForTrain.class);
    }

    private boolean reservationSucceed(ResponseEntity<String> response) {
        return Boolean.logicalAnd(response.getStatusCode() == OK,
                Optional.ofNullable(response.getBody()).filter(not(String::isBlank)).isPresent());
    }
}
