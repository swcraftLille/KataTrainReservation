using TicketOfficeService.Clients;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddHttpClient<IBookingReferenceClient, BookingReferenceClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["BookingReferenceServiceUrl"]!);
});

builder.Services.AddHttpClient<ITrainDataClient, TrainDataClient>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["TrainDataServiceUrl"]!);
});

var app = builder.Build();

app.UseHttpsRedirection();
app.MapControllers();

app.Run();

public partial class Program { }
