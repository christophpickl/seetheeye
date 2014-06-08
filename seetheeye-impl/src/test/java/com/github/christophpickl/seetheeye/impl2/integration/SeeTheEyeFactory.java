package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.test.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.configuration.TestableActionfiedConfiguration;
import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.impl2.Log4j;
import com.github.christophpickl.seetheeye.impl2.SeeTheEye;

final class SeeTheEyeFactory {

    static {
        Log4j.init();
    }

    private SeeTheEyeFactory() {}

    static SeeTheEyeApi newEye(Action1<Configuration> action) {
        Configuration config = new TestableActionfiedConfiguration(action);
        return SeeTheEye.builder().add(config).build();
    }


}
