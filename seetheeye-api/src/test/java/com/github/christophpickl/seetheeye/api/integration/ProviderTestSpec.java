package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.inject.Provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@Test
public abstract class ProviderTestSpec extends BaseTest {

    static class EmptyProvider implements Provider<Beans.Empty> {
        static final Beans.Empty PROVIDING_INSTANCE = new Beans.Empty();
        @Override public Beans.Empty get() { return PROVIDING_INSTANCE; }
    }

    static class Empty2Provider implements Provider<Beans.Empty2> {
        static final Beans.Empty2 PROVIDING_INSTANCE = new Beans.Empty2();
        @Override public Beans.Empty2 get() { return PROVIDING_INSTANCE; }
    }

    public void installProvider_forBeanTypeXAndRequestForBeanX_returnInstanceProvidedByCustomProvider() {
        assertThat(newEye(config -> config.installProvider(EmptyProvider.class)).get(Beans.Empty.class),
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
        assertThat(eye.get(Beans.Empty2.class), instanceOf(Beans.Empty2.class));
        assertThat(eye.get(Beans.Empty.class), instanceOf(Beans.Empty.class));
        assertThat(eye.get(EmptyProvider.class), instanceOf(EmptyProvider.class));
        assertThat(eye.get(Empty2Provider.class), instanceOf(Empty2Provider.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installProvider_sameTypeAsBeanAndAsProvider_throwException() {
        newEye(config -> {
            config.installConcreteBean(Beans.Empty.class);
            config.installProvider(EmptyProvider.class);
        }).get(Beans.Empty.class);
    }

}
