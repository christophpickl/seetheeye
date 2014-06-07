package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.api.test.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.InstallTestSpec;

public class InstallTest extends InstallTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<Configuration> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}
