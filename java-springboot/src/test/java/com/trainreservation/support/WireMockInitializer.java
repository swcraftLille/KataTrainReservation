package com.trainreservation.support;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestContext testContext = TestContext.getInstance();

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                "booking-reference-service.url=" + testContext.getBookingReferenceServiceMock().baseUrl(),
                "train-data-service.url=" + testContext.getTrainDataServiceMock().baseUrl()
        );

        applicationContext.getBeanFactory().registerSingleton("testContext", testContext);
    }
}
