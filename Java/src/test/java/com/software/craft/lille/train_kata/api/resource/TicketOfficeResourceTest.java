package com.software.craft.lille.train_kata.api.resource;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.software.craft.lille.train_kata.DataForTrainFixtures;
import com.software.craft.lille.train_kata.api.external.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.external.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.external.model.DataForTrain;
import com.software.craft.lille.train_kata.api.external.model.TrainCompanyClientException;
import com.software.craft.lille.train_kata.api.resource.request.BookSeatsOnTrainRequest;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import com.software.craft.lille.train_kata.ticket_office.TrainService;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(TicketOfficeResource.class)
@Import(TicketOfficeResourceTest.TicketOfficeResourceTestConfiguration.class)
class TicketOfficeResourceTest {
  private final JsonMapper jsonMapper = new JsonMapper();
  @Autowired private TicketOffice ticketOffice;
  @Autowired private TrainDataServiceClient trainDataServiceClient;
  @Autowired private BookingReferenceClient bookingReferenceClient;
  @Autowired private MockMvc mockMvc;

  @AfterEach
  void tearDown() {
    reset(ticketOffice, trainDataServiceClient, bookingReferenceClient);
  }

  @TestConfiguration
  static class TicketOfficeResourceTestConfiguration {
    @Bean
    public TrainDataServiceClient trainDataServiceClient() {
      return mock(TrainDataServiceClient.class);
    }

    @Bean
    public BookingReferenceClient bookingReferenceClient() {
      return mock(BookingReferenceClient.class);
    }

    @Bean
    public TicketOffice ticketOffice(
        TrainDataServiceClient trainDataServiceClient,
        BookingReferenceClient bookingReferenceClient) {
      return spy(
          new TicketOffice(new TrainService(trainDataServiceClient, bookingReferenceClient)));
    }
  }

  @Nested
  @DisplayName("POST /v1/train/book")
  class BookTrainSeatsTests {
    @Nested
    @DisplayName("OK response when reservation request succeed")
    class Success {
      @Test
      void bookTrain_respondOKWithTicketContainingBookedSeats_onRequestedTrain() throws Exception {
        doReturn(
                new Reservation(
                    "express_2000", List.of(new Seat("A", 1), new Seat("B", 1)), "75bcd15"))
            .when(ticketOffice)
            .makeReservation(any(ReservationRequest.class));

        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {
                                  "train_id": "express_2000",
                                  "number_of_seats": "2"
                                }
                                """))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.train_id", is("express_2000")),
                jsonPath("$.booking_reference", is("75bcd15")),
                jsonPath("$.seats").isArray(),
                jsonPath("$.seats[0]", is("1A")),
                jsonPath("$.seats[1]", is("1B")));

        verify(ticketOffice).makeReservation(new ReservationRequest("express_2000", 2));
      }

      @Test
      void bookTrain_respondOKWithTicketContainingOnlyRequestedTrain_whenBookingNotPossible()
          throws Exception {
        doReturn(new Reservation("express_2000", List.of(), ""))
            .when(ticketOffice)
            .makeReservation(any(ReservationRequest.class));
        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {
                                  "train_id": "express_2000",
                                  "number_of_seats": "2"
                                }
                                """))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.train_id", is("express_2000")),
                jsonPath("$.booking_reference", is("")),
                jsonPath("$.seats").isArray(),
                jsonPath("$.seats").isEmpty());

        verify(ticketOffice).makeReservation(new ReservationRequest("express_2000", 2));
      }
    }

    @Nested
    @DisplayName("Bad request response when reservation request is not valid")
    class BadRequest {
      public static Stream<Arguments> requestWithMissingTrain() {
        return Stream.of(
            Arguments.of(new BookSeatsOnTrainRequest(null, 2)),
            Arguments.of(new BookSeatsOnTrainRequest("", 2)),
            Arguments.of(new BookSeatsOnTrainRequest(" ", 2)));
      }

      public static Stream<Arguments> requestWithInvalidSeat() {
        return Stream.of(
            Arguments.of(new BookSeatsOnTrainRequest("express_2000", null)),
            Arguments.of(new BookSeatsOnTrainRequest("express_2000", -1)));
      }

      @ParameterizedTest(name = "Book train seat from {0} respond BadRequest")
      @MethodSource("requestWithMissingTrain")
      void bookTrain_respondBadRequest_whenNoTrainToBook(
          BookSeatsOnTrainRequest requestWithMissingTrain) throws Exception {
        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(requestWithMissingTrain)))
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.title", is("UnusableData")),
                jsonPath(
                    "$.detail",
                    is(
                        "A train id is required to make a reservation. Current value '%s'"
                            .formatted(requestWithMissingTrain.train()))),
                jsonPath("$.type", is("/v1/train/book")));

        verifyNoInteractions(ticketOffice);
      }

      @ParameterizedTest(name = "Book train seat from {0} respond BadRequest")
      @MethodSource("requestWithInvalidSeat")
      void bookTrain_respondBadRequest_whenNoInvalidNumberOfSeatToBook(
          BookSeatsOnTrainRequest requestWithInvalidSeat) throws Exception {
        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(requestWithInvalidSeat)))
            .andExpect(status().isBadRequest())
            .andExpectAll(
                jsonPath("$.title", is("UnusableData")),
                jsonPath(
                    "$.detail",
                    is(
                        "A positive number of seat to book is required to make a reservation. Current value '%s'"
                            .formatted(requestWithInvalidSeat.numberOfSeats()))),
                jsonPath("$.type", is("/v1/train/book")));

        verifyNoInteractions(ticketOffice);
      }
    }

    @Nested
    @DisplayName("Internal server error response when reservation request failed")
    class InternalServerError {
      @Test
      void bookTrain_respondInternalServerError_whenGettingBookingReferenceFailed()
          throws Exception {
        final DataForTrainFixtures train =
            DataForTrainFixtures.createTrain(
                List.of(
                    new DataForTrain.TrainSeat("A", 1, ""),
                    new DataForTrain.TrainSeat("A", 2, ""),
                    new DataForTrain.TrainSeat("A", 3, "")));
        given(trainDataServiceClient.dataForTrain(anyString()))
            .willReturn(ResponseEntity.ok(jsonMapper.writeValueAsString(train.data())));

        given(bookingReferenceClient.getBookingReference())
            .willThrow(
                new TrainCompanyClientException(
                    new MockClientHttpRequest(
                        HttpMethod.GET, URI.create("http://0.0.0.0:8082/booking_reference")),
                    ProblemDetail.forStatus(502)));

        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {
                                  "train_id": "express_2000",
                                  "number_of_seats": "2"
                                }
                                """))
            .andExpect(status().isInternalServerError())
            .andExpectAll(
                jsonPath("$.title", is("UnreachableData")),
                jsonPath("$.detail", is("Error calling booking reference service")),
                jsonPath("$.type", is("/v1/train/book")));
      }

      @Test
      void bookTrain_respondInternalServerError_whenGettingTrainDataFailed() throws Exception {
        given(bookingReferenceClient.getBookingReference())
            .willReturn(ResponseEntity.ok("75bcd15"));

        given(trainDataServiceClient.dataForTrain(anyString()))
            .willThrow(
                new TrainCompanyClientException(
                    new MockClientHttpRequest(
                        HttpMethod.GET,
                        URI.create("http://0.0.0.0:8081/data_for_train/express_2000")),
                    ProblemDetail.forStatus(400)));

        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {
                                  "train_id": "express_2000",
                                  "number_of_seats": "2"
                                }
                                """))
            .andExpect(status().isInternalServerError())
            .andExpectAll(
                jsonPath("$.title", is("UnreachableData")),
                jsonPath("$.detail", is("Error calling train service")),
                jsonPath("$.type", is("/v1/train/book")));
      }

      @Test
      void bookTrain_respondInternalServerError_whenReservationFromTrainServiceFailed()
          throws Exception {
        given(bookingReferenceClient.getBookingReference())
            .willReturn(ResponseEntity.ok("75bcd15"));
        final DataForTrainFixtures train =
            DataForTrainFixtures.createTrain(
                List.of(
                    new DataForTrain.TrainSeat("A", 1, ""),
                    new DataForTrain.TrainSeat("A", 2, ""),
                    new DataForTrain.TrainSeat("A", 3, "")));
        given(trainDataServiceClient.dataForTrain(anyString()))
            .willReturn(ResponseEntity.ok(jsonMapper.writeValueAsString(train.data())));

        given(trainDataServiceClient.reserveSeats(anyString(), anyString(), anyString()))
            .willThrow(
                new TrainCompanyClientException(
                    new MockClientHttpRequest(
                        HttpMethod.POST, URI.create("http://0.0.0.0:8081//reserve")),
                    ProblemDetail.forStatus(500)));

        mockMvc
            .perform(
                post("/v1/train/book")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {
                                  "train_id": "express_2000",
                                  "number_of_seats": "2"
                                }
                                """))
            .andExpect(status().isInternalServerError())
            .andExpectAll(
                jsonPath("$.title", is("UnreachableData")),
                jsonPath("$.detail", is("Error sending reservation request to train service.")),
                jsonPath("$.type", is("/v1/train/book")));
      }
    }
  }
}
