var builder = WebApplication.CreateBuilder(args);

var app = builder.Build();

app.UseHttpsRedirection();

app.MapPost("/reserve", () =>
{

    return Results.Ok();
});

app.Run();

public partial class Program { }
