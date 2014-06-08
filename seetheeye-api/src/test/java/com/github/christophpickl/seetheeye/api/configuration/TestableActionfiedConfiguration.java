package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.test.Action1;

public class TestableActionfiedConfiguration extends Configuration {

    private final Action1<Configuration> action;

    public TestableActionfiedConfiguration(Action1<Configuration> action) {
        this.action = action;
    }

    @Override
    protected void configure() {
        action.exec(this);
    }

}
