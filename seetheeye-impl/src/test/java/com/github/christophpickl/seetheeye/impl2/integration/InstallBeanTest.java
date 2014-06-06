package com.github.christophpickl.seetheeye.impl2.integration;

import com.github.christophpickl.seetheeye.api.AbstractConfiguration;
import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.integration.InstallBeanTestSpec;

public class InstallBeanTest extends InstallBeanTestSpec {

    @Override protected final SeeTheEyeApi newEye(Action1<AbstractConfiguration> action) {
        return SeeTheEyeFactory.newEye(action);
    }

}
