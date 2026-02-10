using System.Text.Json.Serialization;

namespace TicketOfficeService.Clients;

public record Train(Dictionary<string, SeatDTO> Seats);

public record SeatDTO(
    [property: JsonPropertyName("booking_reference")] string BookingReference,
    [property: JsonPropertyName("seat_number")] string SeatNumber,
    string Coach
);

public record TrainReserveRequestDTO(
    [property: JsonPropertyName("train_id")] string TrainId,
    [property: JsonPropertyName("booking_reference")] string BookingReference,
    List<string> Seats
);

public record ReservationRequestDTO(
    [property: JsonPropertyName("train_id")] string TrainId,
    [property: JsonPropertyName("seat_count")] int SeatCount
);

public record ReservationResponseDTO(List<ReservedSeatDTO> Seats, string BookingId);

public record ReservedSeatDTO(int SeatNumber, string Coach);
