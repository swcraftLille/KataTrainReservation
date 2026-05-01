package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TicketOfficeTest {

    @Test
    public void makeReservation_affectBookingReferenceToTheReservation_fromBookingReferenceService() {
        TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference",
                new TrainDataServiceClient() {
                    @Override
                    public ResponseEntity<String> dataForTrain(String trainId) {
                        return null;
                    }

                    @Override
                    public ResponseEntity<String> reserveSeats(String trainId, String seatsJson, String bookingReference) {
                        return ResponseEntity.ok(
                                """
                                        {
                                          "seats": {}
                                        }
                                        """
                        );
                    }

                    @Override
                    public void resetTrain(String trainId) {

                    }
                });

        Reservation reservation = ticketOffice.makeReservation(new ReservationRequest("1", 1));

        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isEqualTo("new booking reference");
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrain_onTheSameCoach() {
        TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference",
                new TrainDataServiceClient() {
                    @Override
                    public ResponseEntity<String> dataForTrain(String trainId) {
                        return null;
                    }

                    @Override
                    public ResponseEntity<String> reserveSeats(String trainId, String seatsJson, String bookingReference) {
                        return ResponseEntity.ok(
                                """
                                        {
                                          "seats": {
                                            "1a coach": {
                                              "booking_reference": "a booking reference",
                                              "seat_number": "1",
                                              "coach": "a coach"
                                            },
                                            "2a coach": {
                                              "booking_reference": "a booking reference",
                                              "seat_number": "2",
                                              "coach": "a coach"
                                            }
                                          }
                                        }
                                        """
                        );
                    }

                    @Override
                    public void resetTrain(String trainId) {

                    }
                });

        Reservation reservation = ticketOffice.makeReservation(new ReservationRequest("requestedTrain", 2));

        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo("requestedTrain");
        assertThat(reservation.seats()).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(
                        new Seat("a coach", 1),
                        new Seat("a coach", 2)
                );
    }

    @Test
    public void makeReservation_bookTheRequestedNumberOfSeatsOnATrainOnTheSameCoach_usingTrainService() {
        TicketOffice ticketOffice = new TicketOffice(() -> "new booking reference",
                new TrainDataServiceClient() {
                    @Override
                    public ResponseEntity<String> dataForTrain(String trainId) {
                        return null;
                    }

                    @Override
                    public ResponseEntity<String> reserveSeats(String trainId, String seatsJson, String bookingReference) {
                        return ResponseEntity.ok(
                                """
                                        {
                                          "seats": {
                                            "1a coach": {
                                              "booking_reference": "a booking reference",
                                              "seat_number": "1",
                                              "coach": "a coach"
                                            },
                                            "2a coach": {
                                              "booking_reference": "a booking reference",
                                              "seat_number": "2",
                                              "coach": "a coach"
                                            },
                                            "3a coach": {
                                              "booking_reference": "",
                                              "seat_number": "3",
                                              "coach": "a coach"
                                            }
                                          }
                                        }
                                        """
                        );
                    }

                    @Override
                    public void resetTrain(String trainId) {

                    }
                });

        Reservation reservation = ticketOffice.makeReservation(new ReservationRequest("requestedTrain", 2));

        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo("requestedTrain");
        assertThat(reservation.seats()).isNotNull().hasSize(2)
                .containsExactlyInAnyOrder(
                        new Seat("a coach", 1),
                        new Seat("a coach", 2)
                );
    }
}