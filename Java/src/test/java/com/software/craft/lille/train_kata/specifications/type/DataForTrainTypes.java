package com.software.craft.lille.train_kata.specifications.type;

import com.software.craft.lille.train_kata.specifications.type.model.TrainCoach;
import com.software.craft.lille.train_kata.specifications.type.model.TrainCoach.Designation;
import com.software.craft.lille.train_kata.specifications.type.model.TrainCoachs;
import com.software.craft.lille.train_kata.specifications.type.model.TrainIdentifier;
import com.software.craft.lille.train_kata.specifications.type.model.TrainIdentifiers;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.util.Map;
import java.util.stream.Collectors;

public class DataForTrainTypes {
    @DataTableType
    public TrainIdentifiers trainIdentifiers(DataTable dataTable) {
        return new TrainIdentifiers(
                dataTable.entries()
                        .stream()
                        .map(rows -> rows.get("Identifiant du train"))
                        .map(this::trainIdentifier)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    @DataTableType
    public TrainCoachs trainCoachs(DataTable dataTable) {
        return new TrainCoachs(
                dataTable.entries()
                        .stream()
                        .map(this::trainCoach)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    @DataTableType
    public TrainCoach.SeatNumbers seatNumbers(DataTable dataTable) {
        return new TrainCoach.SeatNumbers(
                dataTable.entries()
                        .stream()
                        .map(rows -> rows.get("Siège réservé"))
                        .map(this::seatNumber)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    @ParameterType(".*")
    public TrainIdentifier trainIdentifier(String value) {
        return new TrainIdentifier(value);
    }


    @ParameterType(".*")
    public TrainCoach.Designation coachDesignation(String value) {
        return asCoachDesignation(value);
    }

    @ParameterType(".*")
    public TrainCoach.SeatNumber seatNumber(String value) {
        return new TrainCoach.SeatNumber(value);
    }

    private TrainCoach trainCoach(Map<String, String> rows) {
        return new TrainCoach(asCoachDesignation(rows.get("Voiture")), Integer.parseInt(rows.get("Nombre de sièges")));
    }

    private Designation asCoachDesignation(String value) {
        return new Designation(value);
    }

}
