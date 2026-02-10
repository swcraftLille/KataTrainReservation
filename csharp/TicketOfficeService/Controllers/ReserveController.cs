using Microsoft.AspNetCore.Mvc;
using TicketOfficeService.Clients;

namespace TicketOfficeService.Controllers;

[ApiController]
public class ReserveController : ControllerBase
{
    [HttpPost("/reserve")]
    public IActionResult Reserve([FromBody] ReservationRequestDTO requestDto)
    {
        return Ok();
    }
}
