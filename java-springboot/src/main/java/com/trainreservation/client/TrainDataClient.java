package com.trainreservation.client;

import com.trainreservation.client.dto.TrainDTO;
import com.trainreservation.client.dto.TrainReserveRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class TrainDataClient {

    private final RestClient restClient;

    public TrainDataClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TrainDTO getTrainData(String trainId) {
        return restClient.get()
            .uri("/data_for_train/{trainId}", trainId)
            .retrieve()
            .body(TrainDTO.class);
    }

    public void reserveSeats(TrainReserveRequestDTO request) {
        restClient.post()
            .uri("/reserve")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }
}
