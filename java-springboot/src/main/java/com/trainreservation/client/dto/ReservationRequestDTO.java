package com.trainreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReservationRequestDTO(
    @JsonProperty("train_id") String trainId,
    @JsonProperty("seat_count") int seatCount
) {}
