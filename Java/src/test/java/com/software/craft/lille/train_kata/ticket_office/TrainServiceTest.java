package com.software.craft.lille.train_kata.ticket_office;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
        void reserveSeatsOnTrain_affectBookingReferenceToTheRequestedTrainSeats_thenReturnReservationResult() {
            given(bookingReferenceClient.getBookingReference()).willReturn("booking reference");
            given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString()))
                    .willReturn(ResponseEntity.ok(
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
            final List<Seat> seatsToBook = List.of(new Seat("A", 1), new Seat("B", 2), new Seat("C", 1));

            final DataForTrain dataForTrain = trainService.reserveSeatsOnTrain("a train", seatsToBook);

            assertThat(dataForTrain).isNotNull();
            assertThat(dataForTrain.seats()).hasSize(7);
            assertThat(dataForTrain.seatsBookedWithReference("booking reference"))
                    .containsExactlyInAnyOrderElementsOf(seatsToBook);
        }
    }
}