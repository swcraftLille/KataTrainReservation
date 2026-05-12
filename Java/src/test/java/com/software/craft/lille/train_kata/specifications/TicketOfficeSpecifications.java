package com.software.craft.lille.train_kata.specifications;

import com.software.craft.lille.train_kata.TicketOfficeApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.cucumber.core.options.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.software.craft.lille.train_kata.specifications")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, json:target/cucumber-report/ticket-office.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@RESERVATION")
public class TicketOfficeSpecifications {
    @Testcontainers
    @ActiveProfiles("acceptance-test")
    @CucumberContextConfiguration
    @SpringBootTest(classes = TicketOfficeApplication.class)
    @ContextConfiguration(initializers = TestContainerConfiguration.class)
    public static class TicketOfficeAcceptanceTests {
    }
}