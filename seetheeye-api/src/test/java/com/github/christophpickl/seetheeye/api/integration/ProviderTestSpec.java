package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Beans;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@Test
public abstract class ProviderTestSpec extends BaseTest {

    public void installProvider_forBeanTypeXAndRequestForBeanX_returnInstanceProvidedByCustomProvider() {
        assertThat(newEye(config -> config.installProvider(Beans.EmptyProvider.class)).get(Beans.Empty.class),
                sameInstance(Beans.EmptyProvider.PROVIDING_INSTANCE));
    }

    public void installProvider_requestInstanceOfGivenProvider_returnProviderAsExpected() {
        assertThat(newEye(config -> config.installProvider(Beans.EmptyProvider.class)).get(Beans.EmptyProvider.class),
                notNullValue());
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installProvider_sameTypeAsBeanAndAsProvider_throwException() {
        newEye(config -> {
            config.installConcreteBean(Beans.Empty.class);
            config.installProvider(Beans.EmptyProvider.class);
        }).get(Beans.Empty.class);
    }


}
