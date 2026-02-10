package com.trainreservation.client.dto;

import java.util.List;

public record ReservationResponseDTO(List<ReservedSeatDTO> seats, String bookingId) {}
