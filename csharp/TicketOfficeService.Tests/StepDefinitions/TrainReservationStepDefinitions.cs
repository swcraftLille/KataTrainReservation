using FluentAssertions;
using Reqnroll;
using System;
using System.Collections.Generic;
using System.Linq;
using TicketOfficeService.Tests.Support;
using TicketOfficeService.Tests.TestDoubles;

namespace TicketOfficeService.Tests.StepDefinitions;

[Binding]
public class TrainReservationStepDefinitions
{
    private readonly TestContext _context;
    private TicketOffice _ticketOffice = null!;
    private Reservation _reservation = null!;
    private string _trainId = string.Empty;
    private int _requestedSeats;

    public TrainReservationStepDefinitions(TestContext context)
    {
        _context = context;
    }
    
    [Given(@"the booking reference service is available")]
    public void GivenTheBookingReferenceServiceIsAvailable()
    {
        // Service is already available in TestContext
    }

    [Given(@"the train data service is available")]
    public void GivenTheTrainDataServiceIsAvailable()
    {
        // Service is already available in TestContext
    }

    [Given(@"a train ""([^""]*)"" with the following configuration:")]
    public void GivenATrainWithTheFollowingConfiguration(string trainId, Table table)
    {
        _trainId = trainId;
        var coaches = new List<CoachTestConfig>();
        
        foreach (var row in table.Rows)
        {
            var coach = row["Coach"];
            var totalSeats = int.Parse(row["Total Seats"]);
            var reservedSeats = int.Parse(row["Reserved Seats"]);
            
            coaches.Add(new CoachTestConfig(coach, totalSeats, reservedSeats));
        }
        
        _context.TrainDataService.ConfigureTrainFromTestData(trainId, coaches);
    }

    [When(@"I request to reserve (.*) seats on train ""([^""]*)""")]
    public void WhenIRequestToReserveSeatsOnTrain(int seatCount, string trainId)
    {
        _requestedSeats = seatCount;
        _trainId = trainId;
        
        _ticketOffice = new TicketOffice(
            _context.BookingReferenceService, 
            _context.TrainDataService
        );
        
        var request = new ReservationRequest(trainId, seatCount);
        
        try
        {
            _reservation = _ticketOffice.MakeReservation(request);
        }
        catch (NotImplementedException)
        {
            throw;
        }
    }

    [Then(@"the reservation should be successful")]
    public void ThenTheReservationShouldBeSuccessful()
    {
        _reservation.Should().NotBeNull();
        _reservation.Seats.Should().NotBeEmpty();
        _reservation.Seats.Should().HaveCount(_requestedSeats);
        _reservation.BookingId.Should().NotBeNullOrEmpty();
    }

    [Then(@"all (.*) seats should be in the same coach")]
    public void ThenAllSeatsShouldBeInTheSameCoach(int seatCount)
    {
        _reservation.Seats.Should().HaveCount(seatCount);
        var coaches = _reservation.Seats.Select(s => s.Coach).Distinct();
        coaches.Should().HaveCount(1, "all seats must be in the same coach");
    }

    [Then(@"the reservation should have a booking reference")]
    public void ThenTheReservationShouldHaveABookingReference()
    {
        _reservation.BookingId.Should().NotBeNullOrEmpty();
    }

    [Then(@"the train should be reserved with those seats")]
    public void ThenTheTrainShouldBeReservedWithThoseSeats()
    {
        var trainSeats = _context.TrainDataService.GetTrainSeats(_trainId);
        
        var reservedSeats = trainSeats.Values
            .Where(s => s.BookingReference == _reservation.BookingId)
            .ToList();
        
        reservedSeats.Should().HaveCount(_requestedSeats, 
            "the train data service should have reserved the correct number of seats");
        
        foreach (var seat in _reservation.Seats)
        {
            var seatId = $"{seat.SeatNumber}{seat.Coach}";
            trainSeats.Should().ContainKey(seatId);
            trainSeats[seatId].BookingReference.Should().Be(_reservation.BookingId);
        }
    }
}
