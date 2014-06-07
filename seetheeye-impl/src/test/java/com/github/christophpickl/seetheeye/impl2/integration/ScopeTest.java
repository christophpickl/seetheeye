package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.api.test.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.ScopeTestSpec;

public class ScopeTest extends ScopeTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<Configuration> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}
