package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.api.test.Action1;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
abstract class BaseTest {

    protected abstract SeeTheEyeApi newEye(Action1<Configuration> action);

    protected final void skip(String message) {
        throw new SkipException(message);
    }

    protected static class Empty { }
    protected static class Empty2 { }

    protected interface Interface { }
    protected  static class InterfaceImpl implements Interface { }
    protected interface Interface2 { }
}
