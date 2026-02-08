using TicketOfficeService;

internal class TrainDataService : ITrainDataService
{
    public Task<TrainData> GetTrainDataAsync(string trainId)
    {
        throw new NotImplementedException();
    }

    public Task<bool> ReserveSeatsAsync(string trainId, List<string> seatIds, string bookingReference)
    {
        throw new NotImplementedException();
    }

    public Task ResetAsync(string trainId)
    {
        throw new NotImplementedException();
    }
}