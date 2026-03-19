package com.software.craft.lille.train_kata.configuration.api.booking_reference;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface BookingReferenceClient {
    @GetExchange("/booking_reference")
    String getBookingReference();
}
