package com.software.craft.lille.train_kata.api.resource;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TicketOfficeResource.class)
class TicketOfficeResourceTest {
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
    }
  }
}
