package com.software.craft.lille.train_kata.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = MediaType.APPLICATION_JSON_VALUE,
        contentType = MediaType.APPLICATION_JSON_VALUE)
public interface TrainDataServiceClient {

    @GetExchange(value = "/data_for_train/{trainId}")
    ResponseEntity<String> dataForTrain(@PathVariable("trainId") String trainId);

    @PostExchange(url = "/reserve")
    ResponseEntity<String> reserveSeats(
            @RequestParam("train_id") String trainId,
            @RequestParam("seats") String seatsJson,
            @RequestParam("booking_reference") String bookingReference);

    @PostExchange("/reset/{trainId}")
    void resetTrain(@PathVariable("trainId") String trainId);
}
