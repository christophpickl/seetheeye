package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.inject.Provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@Test(groups = { "Integration", "Provider" })
public abstract class ProviderTestSpec extends BaseTest {

    // provider are always in scope SINGLETON!
    // provider always registers as provider and type it provides -> add validation for that
    // test for providing an interface -> also add validation for that
    //    does NOT provide super types :-p


    static class EmptyProvider implements Provider<Empty> {
        static final Empty PROVIDING_INSTANCE = new Empty();
        @Override public Empty get() { return PROVIDING_INSTANCE; }
    }

    static class Empty2Provider implements Provider<Empty2> {
        static final Empty2 PROVIDING_INSTANCE = new Empty2();
        @Override public Empty2 get() { return PROVIDING_INSTANCE; }
    }

    public void installProvider_forBeanTypeXAndRequestForBeanX_returnInstanceProvidedByCustomProvider() {
        assertThat(newEye(config -> config.installProvider(EmptyProvider.class)).get(Empty.class),
                sameInstance(EmptyProvider.PROVIDING_INSTANCE));
    }

    public void installProvider_requestInstanceOfGivenProvider_returnProviderAsExpected() {
        assertThat(newEye(config -> config.installProvider(EmptyProvider.class)).get(EmptyProvider.class),
                notNullValue());
    }

    public void installProvider_forTwoDifferentBeanTypes_doesntMixUpBecauseOfTypeErasure() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installProvider(EmptyProvider.class);
            config.installProvider(Empty2Provider.class);
        });
        assertThat(eye.get(Empty2.class), instanceOf(Empty2.class));
        assertThat(eye.get(Empty.class), instanceOf(Empty.class));
        assertThat(eye.get(EmptyProvider.class), instanceOf(EmptyProvider.class));
        assertThat(eye.get(Empty2Provider.class), instanceOf(Empty2Provider.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installProvider_sameTypeAsBeanAndAsProvider_throwException() {
        newEye(config -> {
            config.installBean(Empty.class);
            config.installProvider(EmptyProvider.class);
        }).get(Empty.class);
    }

}
