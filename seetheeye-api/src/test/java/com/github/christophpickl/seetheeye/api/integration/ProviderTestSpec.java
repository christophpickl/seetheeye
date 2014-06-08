package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test(groups = { "Integration", "Provider" })
public abstract class ProviderTestSpec extends BaseTest {

    @BeforeMethod
    public void resetConstructorCalledCounter() {
        Empty2Provider.constructorCalled = 0;
    }

    public void installProvider_forBeanTypeXAndRequestForBeanX_returnInstanceProvidedByCustomProvider() {
        assertThat(newEye(config -> config.installProvider(EmptyProvider.class)).get(Empty.class),
                sameInstance(EmptyProvider.PROVIDING_INSTANCE));
    }

    public void installProvider_getProviderHimselfInsteadProvidingType_returnProviderAsExpected() {
        assertThat(newEye(config -> config.installProvider(EmptyProvider.class)).get(EmptyProvider.class),
                instanceOf(EmptyProvider.class));
    }

    public void installProvider_registerForInterfaceType_returnProviderInternalProvidingInstance() {
        assertThat(newEye(config -> config.installProvider(InterfaceProvider.class)).get(Interface.class),
                instanceOf(InterfaceProvider.PROVIDING_INSTANCE.getClass()));
    }

    // unfortunately TestNG does not yet support java8 method references :(
    @Test(dependsOnMethods = { "installProvider_getProviderHimselfInsteadProvidingType_returnProviderAsExpected" })
    public void installProvider_gettingProviderAndProvidedEntityTwoTimes_returnsSameProviderInstanceAgainAsInScopeSingleton() {
        SeeTheEyeApi eye = newEye(config -> config.installProvider(Empty2Provider.class));
        eye.get(Empty2Provider.class);
        eye.get(Empty2Provider.class);
        eye.get(Empty2.class);
        eye.get(Empty2.class);
        assertThat(Empty2Provider.constructorCalled, equalTo(1));
    }

    public void installProvider_forTwoDifferentBeanTypes_doesntMixUpAlthoughOfTypeErasure() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installProvider(EmptyProvider.class);
            config.installProvider(Empty2Provider.class);
        });
        assertThat(eye.get(Empty.class), instanceOf(Empty.class));
        assertThat(eye.get(Empty2.class), instanceOf(Empty2.class));
    }

    public void installProvider_providerWithDependencies_resolvesDependenciesAndReturnsInstance() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(Empty2.class);
            config.installProvider(EmptyProviderWithDependencyForEmpty2.class);
        });
        assertThat(eye.get(EmptyProviderWithDependencyForEmpty2.class).subBean, instanceOf(Empty2.class));
        assertThat(eye.get(Empty.class), sameInstance(EmptyProviderWithDependencyForEmpty2.PROVIDING_INSTANCE));
    }

    public void installProvider_providerWithProviderDependency_resolvesDependencyAndReturnsInstance() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installProvider(Empty2Provider.class);
            config.installProvider(ProviderWithDependencyForProvider.class);
        });
        assertThat(eye.get(Empty.class), sameInstance(ProviderWithDependencyForProvider.PROVIDING_INSTANCE));
        assertThat(eye.get(ProviderWithDependencyForProvider.class).subBean, instanceOf(Empty2Provider.class));
    }


    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installProvider_sameTypeAsBeanAndAsProvider_throwException() {
        newEye(config -> {
            config.installBean(Empty.class);
            config.installProvider(EmptyProvider.class);
        });
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class,
        dependsOnMethods = "installProvider_forBeanTypeXAndRequestForBeanX_returnInstanceProvidedByCustomProvider")
    public void installProvider_providingTypeBySubInterfaceAndGetBySuperInterface_throwUnresolvableException() {
        newEye(config -> config.installProvider(InterfaceSubProvider.class)).get(Interface.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.CyclicDependencyException.class)
    public void installProvider_providerHavingCyclicDependencyOnEachOther_throwException() {
        newEye(config -> {
            config.installProvider(ProviderADependsOnProviderB.class);
            config.installProvider(ProviderBDependsOnProviderA.class);
        });
    }


    private static class EmptyProvider implements Provider<Empty> {
        static final Empty PROVIDING_INSTANCE = new Empty();
        @Override public Empty get() { return PROVIDING_INSTANCE; }
    }

    private static class Empty2Provider implements Provider<Empty2> {
        static int constructorCalled; // watch out to reset the variable before/after running testbeans against this
        static final Empty2 PROVIDING_INSTANCE = new Empty2();
        public Empty2Provider() {
            constructorCalled++;
        }
        @Override public Empty2 get() { return PROVIDING_INSTANCE; }
    }

    private static class InterfaceProvider implements Provider<Interface> {
        static final InterfaceImpl PROVIDING_INSTANCE = new InterfaceImpl();
        @Override
        public Interface get() {
            return PROVIDING_INSTANCE;
        }
    }

    private static class InterfaceSubProvider implements Provider<InterfaceSub> {
        static final InterfaceSubImpl PROVIDING_INSTANCE = new InterfaceSubImpl();
        @Override
        public InterfaceSub get() {
            return PROVIDING_INSTANCE;
        }
    }

    private static class EmptyProviderWithDependencyForEmpty2 implements Provider<Empty> {
        static final Empty PROVIDING_INSTANCE = new Empty();
        final Empty2 subBean;
        EmptyProviderWithDependencyForEmpty2(Empty2 subBean) { this.subBean = subBean; }
        @Override public Empty get() { return PROVIDING_INSTANCE; }
    }

    private static class ProviderWithDependencyForProvider implements Provider<Empty> {
        static final Empty PROVIDING_INSTANCE = new Empty();
        final Provider<Empty2> subBean;
        ProviderWithDependencyForProvider(Provider<Empty2> subBean) { this.subBean = subBean; }
        @Override public Empty get() { return PROVIDING_INSTANCE; }
    }


    private static class ProviderADependsOnProviderB implements Provider<Empty> {
        final ProviderBDependsOnProviderA/*Provider<Empty2>*/ subBean;
        ProviderADependsOnProviderB(ProviderBDependsOnProviderA subBean) { this.subBean = subBean; }
        @Override public Empty get() { return new Empty(); }
    }

    private static class ProviderBDependsOnProviderA implements Provider<Empty2> {
        final ProviderADependsOnProviderB subBean;
        ProviderBDependsOnProviderA(ProviderADependsOnProviderB subBean) { this.subBean = subBean; }
        @Override public Empty2 get() { return new Empty2(); }
    }

}
