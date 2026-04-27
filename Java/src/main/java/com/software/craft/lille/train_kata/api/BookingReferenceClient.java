package com.software.craft.lille.train_kata.api;

import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = MediaType.TEXT_PLAIN_VALUE)
public interface BookingReferenceClient {
    @GetExchange("/booking_reference")
    String getBookingReference();
}
