package com.software.craft.lille.train_kata.specifications;

import com.software.craft.lille.train_kata.configuration.api.booking_reference.BookingReferenceClient;
import com.software.craft.lille.train_kata.configuration.api.train_data_service.TrainDataServiceClient;
import com.software.craft.lille.train_kata.ticket_office.TicketOffice;
import io.cucumber.spring.ScenarioScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class TicketTestConfiguration {
    @Bean
    @ScenarioScope
    public TicketOfficeScenarioState ticketOfficeScenarioState(TrainDataServiceClient trainDataClient,
                                                               BookingReferenceClient bookingReferenceClient,
                                                               TicketOffice ticketOffice,
                                                               JsonMapper jsonMapper) {
        return new TicketOfficeScenarioState( ticketOffice, trainDataClient, bookingReferenceClient, jsonMapper);
    }

}

