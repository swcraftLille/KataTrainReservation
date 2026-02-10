package com.trainreservation.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class TestContext {

    private static TestContext instance;

    private final WireMockServer bookingReferenceServiceMock;
    private final WireMockServer trainDataServiceMock;

    private String responseJson = "";
    private String trainId = "";
    private int requestedSeats;

    private TestContext() {
        bookingReferenceServiceMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        trainDataServiceMock = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        bookingReferenceServiceMock.start();
        trainDataServiceMock.start();
    }

    public static synchronized TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }

    public WireMockServer getBookingReferenceServiceMock() {
        return bookingReferenceServiceMock;
    }

    public WireMockServer getTrainDataServiceMock() {
        return trainDataServiceMock;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public int getRequestedSeats() {
        return requestedSeats;
    }

    public void setRequestedSeats(int requestedSeats) {
        this.requestedSeats = requestedSeats;
    }

    public void reset() {
        bookingReferenceServiceMock.resetAll();
        trainDataServiceMock.resetAll();
        responseJson = "";
        trainId = "";
        requestedSeats = 0;
    }

    public void dispose() {
        bookingReferenceServiceMock.stop();
        trainDataServiceMock.stop();
        instance = null;
    }
}
