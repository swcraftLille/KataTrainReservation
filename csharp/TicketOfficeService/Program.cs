using TicketOfficeService;

var builder = WebApplication.CreateBuilder(args);

// Register services for dependency injection
builder.Services.AddSingleton<IBookingReferenceService, BookingReferenceService>();
builder.Services.AddSingleton<ITrainDataService, TrainDataService>();

var app = builder.Build();

app.UseHttpsRedirection();

app.MapPost("/reserve", (ReserveRequest request, IBookingReferenceService bookingReferenceService, ITrainDataService trainDataService) =>
{
    var ticketOffice = new TicketOffice(bookingReferenceService, trainDataService);
    var reservationRequest = new ReservationRequest(request.train_id, request.seat_count);
    var reservation = ticketOffice.MakeReservation(reservationRequest);

    return Results.Ok(reservation);
});

app.Run();

record ReserveRequest(string train_id, int seat_count);