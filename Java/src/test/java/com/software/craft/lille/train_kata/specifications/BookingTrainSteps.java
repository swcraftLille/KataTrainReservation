package com.software.craft.lille.train_kata.specifications;

import com.software.craft.lille.train_kata.specifications.type.model.DataForTrains;
import com.software.craft.lille.train_kata.specifications.type.model.TrainCoach;
import com.software.craft.lille.train_kata.specifications.type.model.TrainIdentifier;
import com.software.craft.lille.train_kata.specifications.type.model.TrainIdentifiers;
import com.software.craft.lille.train_kata.ticket_office.Reservation;
import com.software.craft.lille.train_kata.ticket_office.Seat;
import io.cucumber.java.After;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Étantdonné;
import io.cucumber.java.fr.Étantdonnéque;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class BookingTrainSteps {
    private final TicketOfficeScenarioState ticketOfficeScenarioState;

    public BookingTrainSteps(TicketOfficeScenarioState ticketOfficeScenarioState) {
        this.ticketOfficeScenarioState = ticketOfficeScenarioState;
    }

    @After
    public void printTrains() {
        ticketOfficeScenarioState.printTrainsState();
    }

    @Étantdonné("les trains de la compagnie ferroviaire")
    public void les_trains_de_la_compagnie_ferroviaire(TrainIdentifiers trainIdentifiers) {
        if (trainIdentifiers.isEmpty()) {
            fail("No train provided.");
        }
        trainIdentifiers.elements().forEach(ticketOfficeScenarioState::resetTrain);
    }

    @Étantdonnéque("le service de réservation est disponible")
    public void le_service_de_réservation_est_disponible() {
        final Optional<String> bookingReference =
                Optional.ofNullable(ticketOfficeScenarioState.newBookingReference())
                        .filter(not(String::isBlank));
        assertThat(bookingReference).isPresent();
    }

    @Étantdonnéque("{int} sièges sont déjà réservés dans la voiture {coachDesignation} du train {trainIdentifier}")
    public void des_sieges_sont_deja_reserves_dans_la_voiture_x_du_train(int numberOfSeats,
                                                                         TrainCoach.Designation coach,
                                                                         TrainIdentifier trainIdentifier) {
        final Set<String> seatsToBook = IntStream.rangeClosed(1, numberOfSeats)
                .mapToObj(seat -> "%s%s".formatted(seat, coach.value()))
                .collect(Collectors.toSet());
        ticketOfficeScenarioState.reserveSeats(trainIdentifier, seatsToBook);
    }

    @Étantdonnéque("chaque voiture du train {trainIdentifier} a 70% de sièges réservés")
    public void chaque_voiture_du_train_compte_a_atteint_le_seuil_de_reservation(TrainIdentifier trainIdentifier) {
        ticketOfficeScenarioState.reservePercentageOfOccupancyInEachTrainCoachs(trainIdentifier, 70);
    }

    @Étantdonnéque("les sièges réservés sur le train {trainIdentifier}")
    public void les_sieges_reserves_sur_le_train_express_(TrainIdentifier trainIdentifier, TrainCoach.SeatNumbers seatNumbers) {
        ticketOfficeScenarioState.reserveSeats(trainIdentifier, seatNumbers.values());
    }

    @Quand("le client réserve {int} places sur le train {trainIdentifier}")
    public void le_client_reserve_des_places_sur_le_train(int numberOfSeat, TrainIdentifier trainIdentifier) {
        ticketOfficeScenarioState.sendReservationRequest(numberOfSeat, trainIdentifier);
    }

    @Alors("une référence de réservation est affectée au client")
    public void une_référence_de_réservation_est_affectée_au_client() {
        final Reservation reservation = assertReservationRequestHasSucceed();
        assertThat(reservation).isNotNull();
        assertThat(reservation.bookingId()).isNotBlank();
    }

    @Alors("les {int} sièges de la réservation appartiennent à la même voiture du train {trainIdentifier}")
    public void les_sieges_de_la_réservation_appartiennent_à_la_même_voiture_du_train(int numberOfBookedSeats, TrainIdentifier trainIdentifier) {
        final Reservation reservation = assertReservationRequestHasSucceed();
        assertThat(reservation).isNotNull();
        assertThat(reservation.seats()).hasSize(numberOfBookedSeats);
        assertBookedSeatsAreOnTheSameCoach(reservation);
        assertTrainBookedSeatsContainsReservationSeats(trainIdentifier, reservation);
    }

    @Alors("aucune référence de réservation n'est affectée au client")
    public void aucune_reference_de_reservation_n_est_affectee_au_client() {
        assertReservationRequestHasSucceed();
        assertThat(ticketOfficeScenarioState.reservationReference()).isEmpty();
    }

    @Alors("aucun nouveau siège n'a été réservé sur le train {trainIdentifier}")
    public void aucun_nouveau_siege_n_a_ete_reserve_sur_le_train_local_(TrainIdentifier trainIdentifier) {
        final Reservation reservation = assertReservationRequestHasSucceed();
        assertThat(reservation).isNotNull().isEqualTo(new Reservation(trainIdentifier.value(), List.of(), ""));
    }

    @Alors("les {int} sièges de la réservation appartiennent à la voiture {coachDesignation} du train {trainIdentifier}")
    public void les_sieges_de_la_reservation_appartiennent_a_la_voiture_x_du_train(int bookedSeatsNumber, TrainCoach.Designation coach, TrainIdentifier trainIdentifier) {
        final Reservation reservation = assertReservationRequestHasSucceed();
        assertThat(reservation).isNotNull();
        assertThat(reservation.trainId()).isEqualTo(trainIdentifier.value());
        long numberOfBookedSeatsInCoach = reservation.seats().stream().filter(seat -> seat.coach().equalsIgnoreCase(coach.value())).count();
        assertThat(numberOfBookedSeatsInCoach).isEqualTo(bookedSeatsNumber);

    }

    private void assertTrainBookedSeatsContainsReservationSeats(TrainIdentifier trainIdentifier, Reservation reservation) {
        final Set<Seat> trainBookedSeats = fetchTrainData(trainIdentifier).seatsWithBookingReference(reservation.bookingId()).stream().map(trainSeat -> new Seat(trainSeat.coach(), Integer.parseInt(trainSeat.seatNumber()))).collect(Collectors.toUnmodifiableSet());
        assertThat(trainBookedSeats).containsAll(reservation.seats());
    }

    private void assertBookedSeatsAreOnTheSameCoach(Reservation reservation) {
        final Set<String> seatsCoach = reservation.seats().stream().map(Seat::coach).collect(Collectors.toSet());
        assertThat(seatsCoach).hasSize(1);
    }

    private Reservation assertReservationRequestHasSucceed() {
        final Optional<Reservation> reservation = ticketOfficeScenarioState.reservation();
        assertThat(reservation).isPresent();
        return reservation.get();
    }

    private DataForTrains fetchTrainData(TrainIdentifier trainIdentifier) {
        final Map<TrainIdentifier, DataForTrains> dataForTrain = ticketOfficeScenarioState.dataForTrain();
        final Optional<DataForTrains> dataForTrainResponse = Optional.ofNullable(dataForTrain.get(trainIdentifier));
        assertThat(dataForTrainResponse).isPresent();
        return dataForTrainResponse.get();
    }
}
