package com.software.craft.lille.train_kata.ticket_office;

import static java.util.function.Predicate.not;
import static org.springframework.http.HttpStatus.OK;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

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

    public DataForTrain dataForTrain(String train) {
        return asDataForTrain(() -> trainDataServiceClient.dataForTrain(train))
                .orElseGet(DataForTrain::empty);
    }

    private boolean seatsHasBeenBooked(String trainId, List<Seat> seats, String bookingReference) {
        final String seatsToBook = jsonMapper.writeValueAsString(seats.stream()
                .map(seat -> "%d%s".formatted(seat.seatNumber(), seat.coach()))
                .toList());
        return bookSeats(trainId, seatsToBook, bookingReference).isPresent();
    }

    private Optional<DataForTrain> bookSeats(String trainId, String seatsToBook, String bookingReference) {
        final Supplier<ResponseEntity<String>> reserveSeatsResponse = () -> trainDataServiceClient.reserveSeats(
                trainId,
                seatsToBook,
                bookingReference);
        return asDataForTrain(reserveSeatsResponse);
    }

    private boolean reservationSucceed(ResponseEntity<String> response) {
        return Boolean.logicalAnd(response.getStatusCode() == OK,
                Optional.ofNullable(response.getBody()).filter(not(String::isBlank)).isPresent());
    }

    private Optional<DataForTrain> asDataForTrain(Supplier<ResponseEntity<String>> data) {
        return Optional.ofNullable(data.get())
                .filter(this::reservationSucceed)
                .map(ResponseEntity::getBody)
                .flatMap(this::readDataForTrain);
    }

    private Optional<DataForTrain> readDataForTrain(String body) {
        try {
            return Optional.ofNullable(jsonMapper.readValue(body, DataForTrain.class));
        } catch (JacksonException jacksonException) {
            logger.warn("[{}] Impossible to get data from '{}'",
                    getClass().getSimpleName(),
                    body,
                    jacksonException);
            return Optional.empty();
        }
    }
}
