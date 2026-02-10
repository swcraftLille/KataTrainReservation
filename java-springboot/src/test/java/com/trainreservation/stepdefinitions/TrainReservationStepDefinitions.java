package com.trainreservation.stepdefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.trainreservation.support.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TrainReservationStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("the booking reference service is available")
    public void theBookingReferenceServiceIsAvailable() {
        context.getBookingReferenceServiceMock().stubFor(
                get(urlEqualTo("/booking_reference"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("75bcd15"))
        );
    }

    @Given("the train data service is available")
    public void theTrainDataServiceIsAvailable() {
        context.getTrainDataServiceMock().stubFor(
                post(urlEqualTo("/reserve"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{}"))
        );
    }

    @Given("a train {string} with the following configuration:")
    public void aTrainWithTheFollowingConfiguration(String trainId, DataTable table) throws JsonProcessingException {
        context.setTrainId(trainId);

        Map<String, Object> seats = new LinkedHashMap<>();

        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String coach = row.get("Coach");
            int totalSeats = Integer.parseInt(row.get("Total Seats"));
            int reservedSeats = Integer.parseInt(row.get("Reserved Seats"));

            for (int i = 1; i <= totalSeats; i++) {
                String seatId = i + coach;
                String bookingRef = i <= reservedSeats ? "existing_booking" : "";
                Map<String, String> seatData = new LinkedHashMap<>();
                seatData.put("booking_reference", bookingRef);
                seatData.put("seat_number", String.valueOf(i));
                seatData.put("coach", coach);
                seats.put(seatId, seatData);
            }
        }

        Map<String, Object> trainData = Map.of("seats", seats);
        String trainJson = objectMapper.writeValueAsString(trainData);

        context.getTrainDataServiceMock().stubFor(
                get(urlEqualTo("/data_for_train/" + trainId))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(trainJson))
        );
    }

    @When("I request to reserve {int} seats on train {string}")
    public void iRequestToReserveSeatsOnTrain(int seatCount, String trainId) {
        context.setRequestedSeats(seatCount);
        context.setTrainId(trainId);

        Map<String, Object> requestBody = Map.of(
                "train_id", trainId,
                "seat_count", seatCount
        );

        String responseJson = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post("http://localhost:" + port + "/reserve")
                .then()
                    .statusCode(200)
                    .extract()
                    .body().asString();

        context.setResponseJson(responseJson);
    }

    @Then("the reservation should be successful")
    public void theReservationShouldBeSuccessful() {
        String responseJson = context.getResponseJson();
        assertThat(responseJson).isNotEmpty();

        List<Object> seats = JsonPath.read(responseJson, "$.seats");
        assertThat(seats).hasSize(context.getRequestedSeats());

        String bookingId = JsonPath.read(responseJson, "$.bookingId");
        assertThat(bookingId).isNotEmpty();
    }

    @Then("all {int} seats should be in the same coach")
    public void allSeatsShouldBeInTheSameCoach(int seatCount) {
        String responseJson = context.getResponseJson();
        assertThat(responseJson).isNotEmpty();

        List<Object> seats = JsonPath.read(responseJson, "$.seats");
        assertThat(seats).hasSize(seatCount);

        List<String> coaches = JsonPath.read(responseJson, "$.seats[*].coach");
        long distinctCoaches = coaches.stream().distinct().count();
        assertThat(distinctCoaches).as("all seats must be in the same coach").isEqualTo(1);
    }

    @Then("the reservation should have a booking reference")
    public void theReservationShouldHaveABookingReference() {
        String responseJson = context.getResponseJson();
        assertThat(responseJson).isNotEmpty();

        String bookingId = JsonPath.read(responseJson, "$.bookingId");
        assertThat(bookingId).isNotEmpty();
    }

    @Then("the train should be reserved with those seats")
    public void theTrainShouldBeReservedWithThoseSeats() {
        String responseJson = context.getResponseJson();

        context.getTrainDataServiceMock().verify(1, postRequestedFor(urlEqualTo("/reserve")));

        var reserveRequests = context.getTrainDataServiceMock()
                .findAll(postRequestedFor(urlEqualTo("/reserve")));

        String requestBody = reserveRequests.get(0).getBodyAsString();

        String requestTrainId = JsonPath.read(requestBody, "$.train_id");
        assertThat(requestTrainId).isEqualTo(context.getTrainId());

        String requestBookingRef = JsonPath.read(requestBody, "$.booking_reference");
        String responseBookingId = JsonPath.read(responseJson, "$.bookingId");
        assertThat(requestBookingRef).isEqualTo(responseBookingId);

        List<String> reservedSeats = JsonPath.read(requestBody, "$.seats");

        List<Map<String, Object>> responseSeatsData = JsonPath.read(responseJson, "$.seats");
        List<String> responseSeats = responseSeatsData.stream()
                .map(seat -> seat.get("seatNumber") + String.valueOf(seat.get("coach")))
                .collect(Collectors.toList());

        assertThat(reservedSeats).hasSize(context.getRequestedSeats());
        assertThat(reservedSeats).containsExactlyInAnyOrderElementsOf(responseSeats);
    }
}
