package com.software.craft.lille.train_kata.api.resource.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;

public record BookSeatsOnTrainRequest(
    @JsonProperty("train_id") String train,
    @JsonProperty("number_of_seats") Integer numberOfSeats) {
  public ReservationRequest toReservationRequest() {
    return new ReservationRequest(this.train, this.numberOfSeats);
  }
}
