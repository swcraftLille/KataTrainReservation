using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using TicketOfficeService;

namespace TicketOfficeService.Tests.TestDoubles;

public class FakeTrainDataService : ITrainDataService
{
    private readonly Dictionary<string, Dictionary<string, SeatData>> _trains = new();

    public void ConfigureTrain(string trainId, Dictionary<string, SeatData> seats)
    {
        _trains[trainId] = new Dictionary<string, SeatData>(seats);
    }

    public void ConfigureTrainFromTestData(string trainId, List<CoachTestConfig> coaches)
    {
        var seats = new Dictionary<string, SeatData>();
        
        foreach (var coach in coaches)
        {
            for (int currentSeatNumber = 1; currentSeatNumber <= coach.TotalSeats; currentSeatNumber++)
            {
                var seatId = $"{currentSeatNumber}{coach.Coach}";
                var bookingRef = currentSeatNumber <= coach.ReservedSeats ? "existing_booking" : string.Empty;
                seats[seatId] = new SeatData(bookingRef, currentSeatNumber.ToString(), coach.Coach);
            }
        }
        
        ConfigureTrain(trainId, seats);
    }

    public Task<TrainData> GetTrainDataAsync(string trainId)
    {
        if (!_trains.ContainsKey(trainId))
        {
            throw new InvalidOperationException($"Train {trainId} not configured");
        }

        return Task.FromResult(new TrainData(_trains[trainId]));
    }

    public Task<bool> ReserveSeatsAsync(string trainId, List<string> seatIds, string bookingReference)
    {
        if (!_trains.ContainsKey(trainId))
        {
            return Task.FromResult(false);
        }

        var train = _trains[trainId];

        foreach (var seatId in seatIds)
        {
            if (!train.ContainsKey(seatId))
            {
                return Task.FromResult(false);
            }

            var seat = train[seatId];
            if (!seat.IsAvailable)
            {
                return Task.FromResult(false);
            }
        }

        foreach (var seatId in seatIds)
        {
            var seat = train[seatId];
            train[seatId] = seat with { BookingReference = bookingReference };
        }

        return Task.FromResult(true);
    }

    public Task ResetAsync(string trainId)
    {
        if (_trains.ContainsKey(trainId))
        {
            var train = _trains[trainId];
            var resetSeats = train.ToDictionary(
                kvp => kvp.Key,
                kvp => kvp.Value with { BookingReference = string.Empty }
            );
            _trains[trainId] = resetSeats;
        }

        return Task.CompletedTask;
    }

    public void Clear()
    {
        _trains.Clear();
    }

    public Dictionary<string, SeatData> GetTrainSeats(string trainId)
    {
        return _trains.ContainsKey(trainId) 
            ? new Dictionary<string, SeatData>(_trains[trainId]) 
            : new Dictionary<string, SeatData>();
    }
}

public record CoachTestConfig(string Coach, int TotalSeats, int ReservedSeats);
