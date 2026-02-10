using System.Net.Http.Json;

namespace TicketOfficeService.Clients;

public interface ITrainDataClient
{
    Task<Train> GetTrainData(string trainId);
    Task ReserveSeats(TrainReserveRequestDTO requestDto);
}

public class TrainDataClient(HttpClient httpClient) : ITrainDataClient
{
    public async Task<Train> GetTrainData(string trainId)
    {
        return await httpClient.GetFromJsonAsync<Train>($"/data_for_train/{trainId}")
            ?? throw new InvalidOperationException("Failed to get train data");
    }

    public async Task ReserveSeats(TrainReserveRequestDTO requestDto)
    {
        var response = await httpClient.PostAsJsonAsync("/reserve", requestDto);
        response.EnsureSuccessStatusCode();
    }
}
