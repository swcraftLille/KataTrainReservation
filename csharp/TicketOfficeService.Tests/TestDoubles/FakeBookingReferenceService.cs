using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using TicketOfficeService;

namespace TicketOfficeService.Tests.TestDoubles;

public class FakeBookingReferenceService : IBookingReferenceService
{
    private int _counter = 0;

    public Task<string> GetBookingReferenceAsync()
    {
        _counter++;
        return Task.FromResult($"75bcd{_counter:D2}");
    }

    public void Reset()
    {
        _counter = 0;
    }
}
