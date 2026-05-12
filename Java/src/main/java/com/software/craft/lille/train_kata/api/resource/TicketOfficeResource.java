package com.software.craft.lille.train_kata.api.resource;

import com.software.craft.lille.train_kata.api.resource.model.Ticket;
import com.software.craft.lille.train_kata.api.resource.request.BookSeatsOnTrainRequest;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/train")
public class TicketOfficeResource {
  private final TicketOffice ticketOffice;

  public TicketOfficeResource(TicketOffice ticketOffice) {
    this.ticketOffice = ticketOffice;
  }

  @PostMapping("/book")
  public ResponseEntity<Ticket> bookSeatsOnTrain(
      @RequestBody BookSeatsOnTrainRequest bookSeatsOntrainRequest) {
    return ResponseEntity.ok(
        Ticket.from(ticketOffice.makeReservation(bookSeatsOntrainRequest.toReservationRequest())));
  }
}
