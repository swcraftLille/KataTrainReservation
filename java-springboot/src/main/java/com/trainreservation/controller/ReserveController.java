package com.trainreservation.controller;

import com.trainreservation.client.dto.ReservationRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReserveController {

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody ReservationRequestDTO request) {
        return ResponseEntity.ok().build();
    }
}
