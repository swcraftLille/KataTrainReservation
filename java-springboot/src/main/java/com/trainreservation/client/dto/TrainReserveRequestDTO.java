package com.trainreservation.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TrainReserveRequestDTO(
    @JsonProperty("train_id") String trainId,
    @JsonProperty("booking_reference") String bookingReference,
    List<String> seats
) {}
