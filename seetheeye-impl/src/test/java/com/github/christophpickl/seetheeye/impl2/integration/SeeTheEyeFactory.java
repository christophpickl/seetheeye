package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.impl2.Log4j;
import com.github.christophpickl.seetheeye.impl2.SeeTheEye;
import com.github.christophpickl.seetheeye.api.configuration.AbstractConfiguration;

final class SeeTheEyeFactory {

    static {
        Log4j.init();
    }

    private SeeTheEyeFactory() {}

    static SeeTheEyeApi newEye(Action1<AbstractConfiguration> action) {
        AbstractConfiguration config = new TestableConfiguration(action);
        return SeeTheEye.builder().add(config).build();
    }


    static class TestableConfiguration extends AbstractConfiguration {

        private final Action1<AbstractConfiguration> action;

        public TestableConfiguration(Action1<AbstractConfiguration> action) {
            this.action = action;
        }

        @Override
        protected void configure() {
            action.exec(this);
        }
    }



}
