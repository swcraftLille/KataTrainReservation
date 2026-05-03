package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainServiceTest {

    @InjectMocks
    private TrainService trainService;
    @Mock
    private TrainDataServiceClient trainDataServiceClient;
    @Mock
    private BookingReferenceClient bookingReferenceClient;

    @Nested
    class ReserveTrainTests {
        @Test
        void reserveSeatsOnTrain_returnReservation_withBookingReferenceForTheRequestedTrainSeats() {
            given(bookingReferenceClient.getBookingReference()).willReturn("booking reference");
            given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString())).willReturn(ResponseEntity.ok("""
                    {
                        "seats": {
                            "1A": {
                               "booking_reference": "booking reference",
                               "seat_number": "1",
                               "coach": "A"
                            },
                            "2A": {
                               "booking_reference": "",
                               "seat_number": "2",
                               "coach": "A"
                            },
                            "1B": {
                               "booking_reference": "",
                               "seat_number": "1",
                               "coach": "B"
                            },
                            "2B": {
                               "booking_reference": "booking reference",
                               "seat_number": "2",
                               "coach": "B"
                            },
                            "3B": {
                               "booking_reference": "",
                               "seat_number": "3",
                               "coach": "B"
                            },
                            "1C": {
                               "booking_reference": "booking reference",
                               "seat_number": "1",
                               "coach": "C"
                            },
                            "2C": {
                               "booking_reference": "",
                               "seat_number": "2",
                               "coach": "C"
                            }
                        }
                    }
                    """));
            final List<Seat> seatsToBook = List.of(
                    new Seat("A", 1),
                    new Seat("B", 2),
                    new Seat("C", 1));

            final Reservation reservation = trainService.reserveSeatsOnTrain("a train", seatsToBook);

            assertThat(reservation).isEqualTo(new Reservation("a train", seatsToBook, "booking reference"));
        }

        @ParameterizedTest(name = "reserveSeatsOnTrain return reservation without booking reference when train service respond OK with body {0}")
        @NullAndEmptySource
        @ValueSource(strings = {
                "already booked with reference: 75bcd15",
                " "
        })
        void reserveSeatsOnTrain_returnReservationWithoutBookingReference_whenResponseOKButBodyIsNotDataForTrain(String body) {
            given(bookingReferenceClient.getBookingReference()).willReturn("booking reference");
            given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString()))
                    .willReturn(ResponseEntity.ok(body));
            final List<Seat> seatsToBook = List.of(
                    new Seat("A", 1),
                    new Seat("B", 2),
                    new Seat("C", 1));

            final Reservation reservation = trainService.reserveSeatsOnTrain("a train", seatsToBook);

            assertThat(reservation).isEqualTo(new Reservation("a train", seatsToBook, ""));
        }

        @ParameterizedTest(name = "reserveSeatsOnTrain return reservation without booking reference when train service respond without OK status: {0}")
        @EnumSource(
                value = HttpStatus.class,
                names = {"OK"},
                mode = EnumSource.Mode.MATCH_NONE)
        void reserveSeatsOnTrain_returnReservationWithoutBookingReference_whenReservationRequestFailed(HttpStatus status) {
            given(bookingReferenceClient.getBookingReference()).willReturn("booking reference");
            given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString()))
                    .willReturn(ResponseEntity.status(status).body(
                            """
                                    {
                                        "seats": {
                                            "1A": {
                                               "booking_reference": "booking reference",
                                               "seat_number": "1",
                                               "coach": "A"
                                            },
                                            "2A": {
                                               "booking_reference": "",
                                               "seat_number": "2",
                                               "coach": "A"
                                            },
                                            "1B": {
                                               "booking_reference": "",
                                               "seat_number": "1",
                                               "coach": "B"
                                            },
                                            "2B": {
                                               "booking_reference": "booking reference",
                                               "seat_number": "2",
                                               "coach": "B"
                                            },
                                            "3B": {
                                               "booking_reference": "",
                                               "seat_number": "3",
                                               "coach": "B"
                                            },
                                            "1C": {
                                               "booking_reference": "booking reference",
                                               "seat_number": "1",
                                               "coach": "C"
                                            },
                                            "2C": {
                                               "booking_reference": "",
                                               "seat_number": "2",
                                               "coach": "C"
                                            }
                                        }
                                    }
                                    """
                    ));
            final List<Seat> seatsToBook = List.of(
                    new Seat("A", 10));

            final Reservation reservation = trainService.reserveSeatsOnTrain("a train", seatsToBook);

            assertThat(reservation).isEqualTo(new Reservation("a train", seatsToBook, ""));
        }
    }

    @Nested
    class DataForTrainTests {
        @Test
        void dataForTrain_returnAllSeatsInformation_forTheRequestedTrain() {
            given(trainDataServiceClient.dataForTrain(anyString())).willReturn(ResponseEntity.ok("""
                    {
                        "seats": {
                            "1A": {
                               "booking_reference": "a booking reference",
                               "seat_number": "1",
                               "coach": "A"
                            },
                            "2A": {
                               "booking_reference": "",
                               "seat_number": "2",
                               "coach": "A"
                            },
                            "1B": {
                               "booking_reference": "",
                               "seat_number": "1",
                               "coach": "B"
                            },
                            "2B": {
                               "booking_reference": "another booking reference",
                               "seat_number": "2",
                               "coach": "B"
                            },
                            "3B": {
                               "booking_reference": "",
                               "seat_number": "3",
                               "coach": "B"
                            },
                            "1C": {
                               "booking_reference": "a third booking reference",
                               "seat_number": "1",
                               "coach": "C"
                            },
                            "2C": {
                               "booking_reference": "",
                               "seat_number": "2",
                               "coach": "C"
                            }
                        }
                    }
                    """));

            final DataForTrain dataForTrain = trainService.dataForTrain("a train");

            verify(trainDataServiceClient).dataForTrain("a train");
            assertThat(dataForTrain).isEqualTo(new DataForTrain(Map.of(
                    "1A", new DataForTrain.TrainSeat("A", 1, "a booking reference"),
                    "2A", new DataForTrain.TrainSeat("A", 2, ""),
                    "1B", new DataForTrain.TrainSeat("B", 1, ""),
                    "2B", new DataForTrain.TrainSeat("B", 2, "another booking reference"),
                    "3B", new DataForTrain.TrainSeat("B", 3, ""),
                    "1C", new DataForTrain.TrainSeat("C", 1, "a third booking reference"),
                    "2C", new DataForTrain.TrainSeat("C", 2, "")
            )));
        }

        @ParameterizedTest(name = "dataForTrain return empty data when train service respond without OK status: {0}")
        @EnumSource(
                value = HttpStatus.class,
                names = {"OK"},
                mode = EnumSource.Mode.MATCH_NONE)
        void dataForTrain_returnEmptyData_whenRequestIsNotOK(HttpStatus status) {
            given(trainDataServiceClient.dataForTrain(anyString()))
                    .willReturn(ResponseEntity.status(status).body("something get wrong"));

            final DataForTrain dataForTrain = trainService.dataForTrain("a train");

            verify(trainDataServiceClient).dataForTrain("a train");
            assertThat(dataForTrain).isEqualTo(DataForTrain.empty());
        }

        @ParameterizedTest(name = "dataForTrain return empty data when train service respond OK with body {0}")
        @NullAndEmptySource
        @ValueSource(strings = {
                " ",
                "a train"
        })
        void dataForTrain_returnEmptyData_whenResponseIsNotDataForTrain(String body) {
            given(trainDataServiceClient.dataForTrain(anyString()))
                    .willReturn(ResponseEntity.ok(body));

            final DataForTrain dataForTrain = trainService.dataForTrain("a train");

            verify(trainDataServiceClient).dataForTrain("a train");
            assertThat(dataForTrain).isEqualTo(DataForTrain.empty());
        }

    }
}