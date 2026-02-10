package com.trainreservation.hooks;

import com.trainreservation.support.TestContext;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class TestHooks {

    @Autowired
    private TestContext context;

    @Before
    public void beforeScenario() {
        context.reset();
    }
}
