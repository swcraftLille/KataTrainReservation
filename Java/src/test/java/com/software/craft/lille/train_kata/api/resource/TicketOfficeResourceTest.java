package com.software.craft.lille.train_kata.api.resource;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.software.craft.lille.train_kata.api.resource.request.BookSeatsOnTrainRequest;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(TicketOfficeResource.class)
class TicketOfficeResourceTest {
  private final JsonMapper jsonMapper = new JsonMapper();
  @MockitoBean private TicketOffice ticketOffice;
  @Autowired private MockMvc mockMvc;

  @Nested
  @DisplayName("POST /v1/train/book")
  class BookTrainSeatsTests {
    @Nested
    @DisplayName("OK response when reservation request succeed")
    class Success {
      @Test
      void bookTrain_respondOKWithTicketContainingBookedSeats_onRequestedTrain() throws Exception {
        given(ticketOffice.makeReservation(any(ReservationRequest.class)))
            .willReturn(
                new Reservation(
                    "express_2000", List.of(new Seat("A", 1), new Seat("B", 1)), "75bcd15"));
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
        given(ticketOffice.makeReservation(any(ReservationRequest.class)))
            .willReturn(new Reservation("express_2000", List.of(), ""));
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
  }
}
