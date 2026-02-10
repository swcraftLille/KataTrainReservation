using System;
using System.Collections.Generic;
using System.Net.Http;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.Configuration;
using WireMock.Server;

namespace TicketOfficeService.Tests.Support;

public class TestContext : IDisposable
{
    public WireMockServer BookingReferenceServiceMock { get; } = WireMockServer.Start();
    public WireMockServer TrainDataServiceMock { get; } = WireMockServer.Start();

    private WebApplicationFactory<Program>? _factory;
    private HttpClient? _client;

    public HttpClient Client
    {
        get
        {
            if (_client != null) return _client;

            _factory = new WebApplicationFactory<Program>()
                .WithWebHostBuilder(builder =>
                {
                    builder.ConfigureAppConfiguration((_, config) =>
                    {
                        config.AddInMemoryCollection(new Dictionary<string, string?>
                        {
                            ["BookingReferenceServiceUrl"] = BookingReferenceServiceMock.Url!,
                            ["TrainDataServiceUrl"] = TrainDataServiceMock.Url!,
                        });
                    });
                });
            _client = _factory.CreateClient();
            return _client;
        }
    }

    public void Reset()
    {
        BookingReferenceServiceMock.Reset();
        TrainDataServiceMock.Reset();
    }

    public void Dispose()
    {
        _client?.Dispose();
        _factory?.Dispose();
        BookingReferenceServiceMock.Dispose();
        TrainDataServiceMock.Dispose();
    }
}
