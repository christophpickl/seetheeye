package com.github.christophpickl.seetheeye.impl.integration;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.InjectTestSpec;
import com.github.christophpickl.seetheeye.api.integration.ScopeTestSpec;

public class ScopeTest extends ScopeTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<Config> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}