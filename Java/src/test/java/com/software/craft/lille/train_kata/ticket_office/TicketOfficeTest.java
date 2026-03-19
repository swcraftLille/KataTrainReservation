package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.configuration.api.booking_reference.BookingReferenceClient;
import com.software.craft.lille.train_kata.configuration.api.train_data_service.TrainDataServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class TicketOfficeTest {
    Reservation reservation = null;

    @Nested
    class MakeReservationWithNullRequest {
        @BeforeEach
        void setUp() {
            TrainDataServiceClient trainDataServiceClient = new TrainDataServiceClient() {
                @Override
                public ResponseEntity<String> dataForTrain(String trainId) {
                    return null;
                }

                @Override
                public void reserveSeats(String trainId, String seatsJson, String bookingReference) {

                }

                @Override
                public void resetTrain(String trainId) {

                }
            };
            BookingReferenceClient bookingReferenceClient = () -> "12345";
            reservation = new TicketOffice(trainDataServiceClient, bookingReferenceClient, new JsonMapper())
                    .makeReservation(null);
        }

        @Test
        public void reservationShouldNotBeNull() {
            assertThat(reservation).isNotNull();
        }

        @Test
        void reservationShouldHaveANotBlankBookId() {
            assertThat(reservation.bookingId()).isNotBlank();
        }

        @Test
        void reservationShouldHave4Seats() {
            assertThat(reservation.seats()).hasSize(4);
        }
    }

    @Test
    void xxx() {
        TrainDataServiceClient trainDataServiceClient = new TrainDataServiceClient() {
            @Override
            public ResponseEntity<String> dataForTrain(String trainId) {
                return null;
            }

            @Override
            public void reserveSeats(String trainId, String seatsJson, String bookingReference) {

            }

            @Override
            public void resetTrain(String trainId) {

            }
        };
        BookingReferenceClient bookingReferenceClient = () -> "12345";
        TicketOffice ticketOffice = new TicketOffice(trainDataServiceClient, bookingReferenceClient, new JsonMapper());

        ReservationRequest reservationRequest = new ReservationRequest("local_1000", 4);

        Reservation reservation = ticketOffice.makeReservation(reservationRequest);

        assertThat(reservation.bookingId()).isEqualTo("12345");
        assertThat(reservation.seats()).hasSize(4);
        assertThat(reservation.trainId()).isEqualTo("local_1000");
    }
}