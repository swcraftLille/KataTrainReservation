namespace TicketOfficeService.Clients;

public interface IBookingReferenceClient
{
    Task<string> GetBookingReference();
}

public class BookingReferenceClient(HttpClient httpClient) : IBookingReferenceClient
{
    public async Task<string> GetBookingReference()
    {
        return await httpClient.GetStringAsync("/booking_reference");
    }
}
