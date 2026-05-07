package com.software.craft.lille.train_kata;

import com.software.craft.lille.train_kata.api.model.DataForTrain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataForTrainFixtures {
  private final Map<String, DataForTrain.TrainSeat> data;

  private DataForTrainFixtures(Map<String, DataForTrain.TrainSeat> data) {
    this.data = data;
  }

  public static DataForTrainFixtures createTrain(List<DataForTrain.TrainSeat> seats) {
    final Map<String, DataForTrain.TrainSeat> data = HashMap.newHashMap(seats.size());
    seats.forEach(seat -> data.put("%d%s".formatted(seat.seatNumber(), seat.coach()), seat));
    return new DataForTrainFixtures(data);
  }

  public DataForTrainFixtures addSeats(List<DataForTrain.TrainSeat> trainSeats) {
    trainSeats.forEach(seat -> data.put("%d%s".formatted(seat.seatNumber(), seat.coach()), seat));
    return new DataForTrainFixtures(data);
  }

  public DataForTrain data() {
    return new DataForTrain(data);
  }
}
