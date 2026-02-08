using Reqnroll;
using TicketOfficeService.Tests.Support;

namespace TicketOfficeService.Tests.Hooks;

[Binding]
public class TestHooks
{
    private readonly TestContext _context;

    public TestHooks(TestContext context)
    {
        _context = context;
    }

    [BeforeScenario]
    public void BeforeScenario()
    {
        _context.Reset();
    }

    [AfterScenario]
    public void AfterScenario()
    {
        _context.Reset();
    }
}
