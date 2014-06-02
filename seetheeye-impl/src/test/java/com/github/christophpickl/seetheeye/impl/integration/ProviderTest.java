package com.github.christophpickl.seetheeye.impl.integration;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.ProviderTestSpec;

public class ProviderTest extends ProviderTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<Config> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}
