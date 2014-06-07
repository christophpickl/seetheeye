package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.impl2.Log4j;
import com.github.christophpickl.seetheeye.impl2.SeeTheEye;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Test(groups = { "Integration" })
public class FullIntegrationTest {

    static {
        Log4j.init();
    }

    public void full() {
        SeeTheEyeApi eye = SeeTheEye.builder().add(new TestConfiguration()).build();
        assertThat(eye.get(Bean.class), notNullValue());
    }

    static class TestConfiguration extends Configuration {
        @Override
        protected void configure() {
            installBean(Bean.class);
        }
    }

    static class Bean { }

}
