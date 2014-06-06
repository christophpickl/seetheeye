package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.AbstractConfiguration;
import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.ScopeTestSpec;

public class ScopeTest extends ScopeTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<AbstractConfiguration> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}
