package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import org.springframework.http.ResponseEntity;

import java.util.StringJoiner;

public class StubTrainService implements TrainDataServiceClient {
    private final ReservationRequest reservationRequest;

    public StubTrainService(ReservationRequest reservationRequest) {
        this.reservationRequest = reservationRequest;
    }

    @Override
    public ResponseEntity<String> dataForTrain(String trainId) {
        return null;
    }

    @Override
    public ResponseEntity<String> reserveSeats(String trainId, String seatsJson, String bookingReference) {
        final StringJoiner responseBody = new StringJoiner("");
        responseBody.add("""
                {
                    "seats": {
                """);
        for (int seatNumber = 1; seatNumber <= 10; seatNumber++) {
            if (seatNumber <= reservationRequest.seatCount()) {
                responseBody.add("""
                        "%dA": {
                                    "booking_reference": "%s",
                                    "seat_number": "%d",
                                    "coach": "A"
                                }
                        """.formatted(seatNumber, bookingReference, seatNumber));
            } else {
                responseBody.add("""
                        "%dA": {
                                    "booking_reference": "",
                                    "seat_number": "%d",
                                    "coach": "A"
                                }
                        """.formatted(seatNumber, seatNumber));
            }
            if (seatNumber < 10) {
                responseBody.add(",");
            }
        }
        responseBody.add("}").add("}");
        return ResponseEntity.ok(responseBody.toString());
    }

    @Override
    public void resetTrain(String trainId) {

    }
}
