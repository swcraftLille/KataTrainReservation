package com.software.craft.lille.train_kata.configuration.http_client;

import com.software.craft.lille.train_kata.configuration.api.TrainCompanyClientException;
import com.software.craft.lille.train_kata.configuration.api.booking_reference.BookingReferenceClient;
import com.software.craft.lille.train_kata.configuration.api.train_data_service.TrainDataServiceClient;
import com.software.craft.lille.train_kata.configuration.properties.BookingReferenceServiceProperties;
import com.software.craft.lille.train_kata.configuration.properties.HttpClientProperties;
import com.software.craft.lille.train_kata.configuration.properties.TrainDataServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties({TrainDataServiceProperties.class, BookingReferenceServiceProperties.class})
public class HttpClientsConfiguration {
    private final Logger logger = LoggerFactory.getLogger(HttpClientsConfiguration.class);
    private final TrainDataServiceProperties trainDataServiceProperties;
    private final BookingReferenceServiceProperties bookingReferenceServiceProperties;

    public HttpClientsConfiguration(TrainDataServiceProperties trainDataServiceProperties, BookingReferenceServiceProperties bookingReferenceServiceProperties) {
        this.trainDataServiceProperties = trainDataServiceProperties;
        this.bookingReferenceServiceProperties = bookingReferenceServiceProperties;
    }

    @Bean
    public TrainDataServiceClient trainDataClient(final JsonMapper jsonMapper) {
        logger.info("[{}] Creating HTTP client for train data service on {}",
                getClass().getSimpleName(),
                trainDataServiceProperties.baseUrl());
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(buildClient(jsonMapper, trainDataServiceProperties)))
                .build()
                .createClient(TrainDataServiceClient.class);
    }

    @Bean
    public BookingReferenceClient bookingReferenceClient(JsonMapper jsonMapper) {
        logger.info("[{}] Creating HTTP client for booking reference service on {}",
                getClass().getSimpleName(),
                bookingReferenceServiceProperties.baseUrl());
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(buildClient(jsonMapper, bookingReferenceServiceProperties)))
                .build()
                .createClient(BookingReferenceClient.class);
    }

    private RestClient buildClient(JsonMapper jsonMapper, HttpClientProperties clientProperties) {
        return RestClient.builder()
                .baseUrl(clientProperties.baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, errorHandler(jsonMapper))
                .build();
    }

    private RestClient.ResponseSpec.ErrorHandler errorHandler(final JsonMapper jsonMapper) {
        return (request, response) -> {
            try (InputStream in = response.getBody()) {
                final ProblemDetail problem = jsonMapper.readValue(in, ProblemDetail.class);
                logger.error("HTTP error {} {}: {}", problem.getStatus(), problem.getTitle(), problem.getDetail());
                throw new TrainCompanyClientException(request, problem);
            } catch (IOException e) {
                throw new TrainCompanyClientException(request, ProblemDetail.forStatus(response.getStatusCode()));
            }
        };
    }
}
