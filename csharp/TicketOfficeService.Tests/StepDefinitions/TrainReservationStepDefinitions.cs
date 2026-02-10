using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Json;
using System.Text.Json;
using System.Threading.Tasks;
using FluentAssertions;
using Reqnroll;
using TicketOfficeService.Tests.Support;
using WireMock.RequestBuilders;
using WireMock.ResponseBuilders;

namespace TicketOfficeService.Tests.StepDefinitions;

[Binding]
public class TrainReservationStepDefinitions(TestContext context)
{
    private string _responseJson = string.Empty;
    private string _trainId = string.Empty;
    private int _requestedSeats;

    [Given(@"the booking reference service is available")]
    public void GivenTheBookingReferenceServiceIsAvailable()
    {
        context.BookingReferenceServiceMock
            .Given(Request.Create().WithPath("/booking_reference").UsingGet())
            .RespondWith(Response.Create().WithStatusCode(200).WithBody("75bcd15"));
    }

    [Given(@"the train data service is available")]
    public void GivenTheTrainDataServiceIsAvailable()
    {
        context.TrainDataServiceMock
            .Given(Request.Create().WithPath("/reserve").UsingPost())
            .RespondWith(Response.Create().WithStatusCode(200).WithBody("{}"));
    }

    [Given(@"a train ""([^""]*)"" with the following configuration:")]
    public void GivenATrainWithTheFollowingConfiguration(string trainId, Table table)
    {
        _trainId = trainId;

        var seats = new Dictionary<string, object>();

        foreach (var row in table.Rows)
        {
            var coach = row["Coach"];
            var totalSeats = int.Parse(row["Total Seats"]);
            var reservedSeats = int.Parse(row["Reserved Seats"]);

            for (int i = 1; i <= totalSeats; i++)
            {
                var seatId = $"{i}{coach}";
                var bookingRef = i <= reservedSeats ? "existing_booking" : "";
                seats[seatId] = new { booking_reference = bookingRef, seat_number = i.ToString(), coach };
            }
        }

        var trainJson = JsonSerializer.Serialize(new { seats });

        context.TrainDataServiceMock
            .Given(Request.Create().WithPath($"/data_for_train/{trainId}").UsingGet())
            .RespondWith(Response.Create()
                .WithStatusCode(200)
                .WithHeader("Content-Type", "application/json")
                .WithBody(trainJson));
    }

    [When(@"I request to reserve (.*) seats on train ""([^""]*)""")]
    public async Task WhenIRequestToReserveSeatsOnTrain(int seatCount, string trainId)
    {
        _requestedSeats = seatCount;
        _trainId = trainId;

        var response = await context.Client.PostAsJsonAsync("/reserve", new
        {
            train_id = trainId,
            seat_count = seatCount
        });

        response.EnsureSuccessStatusCode();
        _responseJson = await response.Content.ReadAsStringAsync();
    }

    [Then(@"the reservation should be successful")]
    public void ThenTheReservationShouldBeSuccessful()
    {
        _responseJson.Should().NotBeNullOrEmpty();
        
        var json = JsonDocument.Parse(_responseJson).RootElement;

        json.GetProperty("seats").GetArrayLength().Should().Be(_requestedSeats);
        json.GetProperty("bookingId").GetString().Should().NotBeNullOrEmpty();
    }

    [Then(@"all (.*) seats should be in the same coach")]
    public void ThenAllSeatsShouldBeInTheSameCoach(int seatCount)
    {
        _responseJson.Should().NotBeNullOrEmpty();
        
        var json = JsonDocument.Parse(_responseJson).RootElement;
        var seats = json.GetProperty("seats").EnumerateArray().ToList();

        seats.Should().HaveCount(seatCount);

        var coaches = seats.Select(s => s.GetProperty("coach").GetString()).Distinct();
        coaches.Should().HaveCount(1, "all seats must be in the same coach");
    }

    [Then(@"the reservation should have a booking reference")]
    public void ThenTheReservationShouldHaveABookingReference()
    {
        _responseJson.Should().NotBeNullOrEmpty();
        
        var json = JsonDocument.Parse(_responseJson).RootElement;
        json.GetProperty("bookingId").GetString().Should().NotBeNullOrEmpty();
    }

    [Then(@"the train should be reserved with those seats")]
    public void ThenTheTrainShouldBeReservedWithThoseSeats()
    {
        var reserveRequests = context.TrainDataServiceMock.LogEntries
            .Where(e => e.RequestMessage is { Path: "/reserve", Method: "POST" })
            .ToList();

        reserveRequests.Should().HaveCount(1, "TicketOffice should have called POST /reserve exactly once");

        var body = JsonDocument.Parse(reserveRequests[0].RequestMessage.Body!).RootElement;
        var response = JsonDocument.Parse(_responseJson).RootElement;

        body.GetProperty("train_id").GetString().Should().Be(_trainId);
        body.GetProperty("booking_reference").GetString().Should().Be(
            response.GetProperty("bookingId").GetString());

        var reservedSeats = body.GetProperty("seats").EnumerateArray()
            .Select(s => s.GetString())
            .ToList();

        var responseSeats = response.GetProperty("seats").EnumerateArray()
            .Select(s => $"{s.GetProperty("seatNumber").GetInt32()}{s.GetProperty("coach").GetString()}")
            .ToList();

        reservedSeats.Should().HaveCount(_requestedSeats);
        reservedSeats.Should().BeEquivalentTo(responseSeats);
    }
}
