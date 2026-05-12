package com.software.craft.lille.train_kata.specifications;

import com.software.craft.lille.train_kata.ticket_office.Seat;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableSet;

public class BookingTrainType {
    @ParameterType(".*")
    public TrainId trainId(String value) {
        return new TrainId(value);
    }

    @DataTableType
    public Collection<Seat> seats(DataTable dataTable) {
        return dataTable.entries().stream().map(this::toSeat).collect(toUnmodifiableSet());
    }

    private Seat toSeat(Map<String, String> row) {
        final String seats = row.get("Sièges");
        return new Seat(
                seats.substring(1, seats.length() - 1),
                Integer.parseInt(seats.substring(0, seats.length() - 1)));
    }
}
