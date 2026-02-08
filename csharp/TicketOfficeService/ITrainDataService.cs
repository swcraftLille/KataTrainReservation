using System.Collections.Generic;

namespace TicketOfficeService;

public interface ITrainDataService
{
    Task<TrainData> GetTrainDataAsync(string trainId);
    Task<bool> ReserveSeatsAsync(string trainId, List<string> seatIds, string bookingReference);
    Task ResetAsync(string trainId);
}

public record TrainData(Dictionary<string, SeatData> Seats);

public record SeatData(string BookingReference, string SeatNumber, string Coach)
{
    public bool IsAvailable => string.IsNullOrEmpty(BookingReference);
}
