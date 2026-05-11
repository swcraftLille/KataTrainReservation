package com.software.craft.lille.train_kata.specifications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.software.craft.lille.train_kata.api.BookingReferenceClient;
import com.software.craft.lille.train_kata.api.TrainDataServiceClient;
import com.software.craft.lille.train_kata.api.model.DataForTrain;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import com.software.craft.lille.train_kata.ticket_office.reservation.Reservation;
import com.software.craft.lille.train_kata.ticket_office.reservation.ReservationRequest;
import com.software.craft.lille.train_kata.ticket_office.reservation.Seat;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonné;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.json.JsonMapper;

public class BookingTrainSteps {
    private static final Logger log = LoggerFactory.getLogger(BookingTrainSteps.class);
    private final JsonMapper jsonMapper = new JsonMapper();
    private final TrainDataServiceClient trainDataServiceClient;
    private final BookingReferenceClient bookingReferenceClient;
    private final TicketOffice ticketOffice;
    private Reservation reservation;
    private DataForTrain dataForTrainBeforeReservation;

    public BookingTrainSteps(
            TrainDataServiceClient trainDataServiceClient,
            BookingReferenceClient bookingReferenceClient,
            TicketOffice ticketOffice) {
        this.trainDataServiceClient = trainDataServiceClient;
        this.bookingReferenceClient = bookingReferenceClient;
        this.ticketOffice = ticketOffice;
    }

    @Before
    public void setup() {
        List.of("local_1000", "express_2000").forEach(trainDataServiceClient::resetTrain);
    }

    @Étantdonné("le train {trainId}")
    public void le_train(TrainId train, DataTable dataTable) {
        for (Map<String, String> entry : dataTable.entries()) {
            final String coach = entry.get("Voiture");
            final String[] seatsOnCoach = entry.get("Sièges").split("-");
            final List<String> seatToBookOnCoach = new ArrayList<>();
            for (int seatNumber = 1; seatNumber <= seatsOnCoach.length; seatNumber++) {
                final String seat = seatsOnCoach[seatNumber - 1];
                if (seat.equalsIgnoreCase("X")) {
                    seatToBookOnCoach.add("%d%s".formatted(seatNumber, coach));
                }
            }
            if (!seatToBookOnCoach.isEmpty()) {
                try {
                    final ResponseEntity<String> response =
                            trainDataServiceClient.reserveSeats(
                                    train.value(),
                                    jsonMapper.writeValueAsString(seatToBookOnCoach),
                                    bookingReferenceClient.getBookingReference());
                    if (response.getStatusCode().isError()) {
                        fail("La réservation sur la train '%s' a échouée".formatted(train.value()));
                    }
                } catch (JsonProcessingException jsonProcessingException) {
                    fail(
                            "Impossible de définir la liste de sièges à réserver sur le train '%s'"
                                    .formatted(train),
                            jsonProcessingException);
                }
            }
        }
        asDataForTrain(trainDataServiceClient.dataForTrain(train.value()).getBody())
                .ifPresent(dataForTrain -> dataForTrainBeforeReservation = dataForTrain);
    }

    @Quand("le client réserve {int} sièges sur le train {trainId}")
    public void le_client_reserve_X_sieges_sur_le_train(int numberOfSeatsToBook, TrainId train) {
        reservation =
                ticketOffice.makeReservation(new ReservationRequest(train.value(), numberOfSeatsToBook));
    }

    @Alors("une référence de réservation est affectée au client")
    public void une_reference_de_reservation_est_affectee_au_client() {
        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isNotNull().isNotBlank();
    }

    @Alors(
            "les places de la réservation sur le train {trainId} appartiennent toutes à la même voiture")
    public void les_places_de_la_reservation_sur_le_train_appartiennent_toutes_a_la_meme_voiture(
            TrainId train) {
        assertThat(reservation.seats()).isNotNull().isNotEmpty();
        List<String> bookedSeatCoachs =
                reservation.seats().stream().map(Seat::coach).distinct().toList();
        assertThat(bookedSeatCoachs).hasSize(1);
        final Optional<DataForTrain> dataForTrain =
                asDataForTrain(trainDataServiceClient.dataForTrain(train.value()).getBody());
        assertThat(dataForTrain).isPresent();
        final Set<String> coachWithBookingReferenceOnTrain =
                dataForTrain.get().seats().values().stream()
                        .filter(trainSeat -> trainSeat.bookingReference().equalsIgnoreCase(reservation.bookingId()))
                        .map(DataForTrain.TrainSeat::coach)
                        .collect(Collectors.toSet());
        assertThat(coachWithBookingReferenceOnTrain).containsExactly(bookedSeatCoachs.getFirst());
    }

    @Alors("les places de la réservation sur le train {trainId} sont")
    public void les_places_de_la_reservation_sur_le_train_sont(
            TrainId train, Collection<Seat> seats) {
        assertThat(reservation).isNotNull();
        assertThat(reservation.seats()).isNotEmpty();
        assertThat(reservation.seats()).containsExactlyInAnyOrderElementsOf(seats);
        final Optional<DataForTrain> dataForTrain =
                asDataForTrain(trainDataServiceClient.dataForTrain(train.value()).getBody());
        assertThat(dataForTrain).isPresent();
        final Set<Seat> seatsWithBookingReferenceOnTrain =
                dataForTrain.get().seats().values().stream()
                        .filter(trainSeat -> trainSeat.bookingReference().equalsIgnoreCase(reservation.bookingId()))
                        .map(trainSeat -> new Seat(trainSeat.coach(), trainSeat.seatNumber()))
                        .collect(Collectors.toSet());
        assertThat(seatsWithBookingReferenceOnTrain).containsExactlyInAnyOrderElementsOf(seats);
    }

    @Alors("aucune référence de réservation n'est affectée au client")
    public void aucune_reference_de_reservation_n8est_affectee_au_client() {
        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isNotNull().isBlank();
    }

    @Alors("aucun siège n'a été réservé sur le train {trainId}")
    public void aucun_siege_n_a_ete_reserve_sur_le_train(TrainId trainId) {
    assertThat(reservation).isNotNull();
    assertThat(reservation.seats()).isEmpty();
        final Optional<DataForTrain> dataForTrain =
                asDataForTrain(trainDataServiceClient.dataForTrain(trainId.value()).getBody());
        assertThat(dataForTrain).isPresent();
        assertThat(dataForTrain.get()).isEqualTo(dataForTrainBeforeReservation);
    }

    private Optional<DataForTrain> asDataForTrain(String body) {
        try {
            return Optional.of(jsonMapper.readValue(body, DataForTrain.class));
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Impossible de transformer les données du train", jsonProcessingException);
            return Optional.empty();
        }
    }
}
