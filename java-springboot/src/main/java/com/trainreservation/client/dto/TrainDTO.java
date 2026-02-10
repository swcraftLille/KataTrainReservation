package com.trainreservation.client.dto;

import java.util.Map;

public record TrainDTO(Map<String, SeatDTO> seats) {}
