package com.software.craft.lille.train_kata.specifications;

import com.software.craft.lille.train_kata.configuration.api.booking_reference.BookingReferenceClient;
import com.software.craft.lille.train_kata.configuration.api.train_data_service.TrainDataServiceClient;
import com.software.craft.lille.train_kata.specifications.type.model.DataForTrains;
import com.software.craft.lille.train_kata.specifications.type.model.TrainIdentifier;
import com.software.craft.lille.train_kata.specifications.type.model.TrainSeat;
import com.software.craft.lille.train_kata.ticket_office.Reservation;
import com.software.craft.lille.train_kata.ticket_office.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class TicketOfficeScenarioState {
    private final Logger logger = LoggerFactory.getLogger(TicketOfficeScenarioState.class);
    private final Map<TrainIdentifier, DataForTrains> dataForTrains = new HashMap<>();

    private final TicketOffice ticketOffice;
    private final TrainDataServiceClient trainDataClient;
    private final BookingReferenceClient bookingReferenceClient;
    private final JsonMapper jsonMapper;

    private Reservation reservation;

    public TicketOfficeScenarioState(TicketOffice ticketOffice, TrainDataServiceClient trainDataClient,
                                     BookingReferenceClient bookingReferenceClient,
                                     JsonMapper jsonMapper) {
        this.ticketOffice = ticketOffice;
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
        this.jsonMapper = jsonMapper;
    }

    public Map<TrainIdentifier, DataForTrains> dataForTrain() {
        return dataForTrains;
    }

    public void sendReservationRequest(int numberOfSeat, TrainIdentifier trainIdentifier) {
        printTrainsState();
        reservation = ticketOffice.makeReservation(new ReservationRequest(trainIdentifier.value(), numberOfSeat));
        refreshDataForTrain(trainIdentifier);
    }

    public Optional<String> reservationReference() {
        return reservation()
                .map(Reservation::bookingId)
                .filter(not(String::isBlank));
    }

    public Optional<Reservation> reservation() {
        return Optional.ofNullable(reservation);
    }

    public String newBookingReference() {
        return bookingReferenceClient.getBookingReference();
    }

    public void resetTrain(TrainIdentifier trainIdentifier) {
        trainDataClient.resetTrain(trainIdentifier.value());
        refreshDataForTrain(trainIdentifier);
    }

    public void reserveSeats(TrainIdentifier trainIdentifier, Set<String> seatsToBook) {
        trainDataClient.reserveSeats(
                trainIdentifier.value(),
                jsonMapper.writeValueAsString(seatsToBook),
                newBookingReference());
        refreshDataForTrain(trainIdentifier);
    }

    public void reservePercentageOfOccupancyInEachTrainCoachs(TrainIdentifier trainIdentifier, int occupancy) {
        double percentage = (double) occupancy / 100;
        fetchDataForTrain(trainIdentifier)
                .seats().values()
                .stream()
                .collect(Collectors.groupingBy(TrainSeat::coach))
                .forEach((coach, seats) -> {
                    final long maxNumberOfBookedSeats = Math.round(seats.size() * percentage);
                    final Set<String> seatsToBook = seats.subList(0, Math.toIntExact(maxNumberOfBookedSeats))
                            .stream()
                            .map(seat -> "%s%s".formatted(seat.seatNumber(), coach))
                            .collect(Collectors.toSet());
                    reserveSeats(trainIdentifier, seatsToBook);
                });
    }

    public void printTrainsState() {
        final StringJoiner trains = new StringJoiner(System.lineSeparator());
        dataForTrains.forEach((trainIdentifier, dataForTrainResponse) ->
                trains.add(
                        new StringJoiner(System.lineSeparator())
                                .add("")
                                .add(trainIdentifier.value())
                                .add(dataForTrainResponse.print())
                                .toString()));
        logger.info(trains.toString());
    }

    private void refreshDataForTrain(TrainIdentifier trainIdentifier) {
        final DataForTrains response = fetchDataForTrain(trainIdentifier);
        dataForTrains.put(trainIdentifier, response);
    }

    private DataForTrains fetchDataForTrain(TrainIdentifier trainIdentifier) {
        return jsonMapper.readValue(
                trainDataClient.dataForTrain(trainIdentifier.value()).getBody(),
                DataForTrains.class);
    }
}
