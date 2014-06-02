package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
abstract class BaseTest {

    protected abstract SeeTheEyeApi newEye(Action1<Config> action);


    protected final void skip(String message) {
        throw new SkipException(message);
    }

}
