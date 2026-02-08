using System.Collections.Generic;
using System.Linq;
using TicketOfficeService.Tests.TestDoubles;

namespace TicketOfficeService.Tests.Support;

/// <summary>
/// Shared context for test scenarios to store state between steps
/// </summary>
public class TestContext
{
    public FakeBookingReferenceService BookingReferenceService { get; } = new();
    public FakeTrainDataService TrainDataService { get; } = new();

    public void Reset()
    {
        BookingReferenceService.Reset();
        TrainDataService.Clear();
    }
}

public class TrainTestData
{
    public string TrainId { get; set; } = string.Empty;
    public List<CoachTestData> Coaches { get; set; } = new();
    
    public int TotalSeats => Coaches.Sum(c => c.TotalSeats);
    public int ReservedSeats => Coaches.Sum(c => c.ReservedSeats);
    public int AvailableSeats => TotalSeats - ReservedSeats;
}

public class CoachTestData
{
    public string Coach { get; set; } = string.Empty;
    public int TotalSeats { get; set; }
    public int ReservedSeats { get; set; }
    public List<SeatTestData> Seats { get; set; } = new();
}

public class SeatTestData
{
    public string SeatId { get; set; } = string.Empty;
    public string Coach { get; set; } = string.Empty;
    public int SeatNumber { get; set; }
    public string BookingReference { get; set; } = string.Empty;
}
