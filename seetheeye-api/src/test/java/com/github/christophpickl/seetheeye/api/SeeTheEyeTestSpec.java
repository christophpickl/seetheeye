package com.github.christophpickl.seetheeye.api;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Test
public abstract class SeeTheEyeTestSpec {

    @BeforeMethod
    public void init() {
        Beans.ConstructorCounting.constructorCalled = 0;
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void resolvingUnknownBeanShouldThrowException() {
        newEye(config -> {}).get(Object.class);
    }

    public void installingConcreteBeanShouldReturnInstalledBeanAgain() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.Empty.class));
        eye.get(Beans.Empty.class);
    }

    public void installingPrototypeScopedBeanShouldConstructNewBeanAllTheTime() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.ConstructorCounting.class));
        eye.get(Beans.ConstructorCounting.class);
        eye.get(Beans.ConstructorCounting.class);
        assertThat(Beans.ConstructorCounting.constructorCalled, equalTo(2));
    }

    public void installingSingletonScopedBeanShouldConstructBeanOnlyOnce() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.ConstructorCounting.class).inScope(Scope.SINGLETON));
        eye.get(Beans.ConstructorCounting.class);
        eye.get(Beans.ConstructorCounting.class);
        assertThat(Beans.ConstructorCounting.constructorCalled, equalTo(1));
    }

    protected abstract SeeTheEyeApi newEye(Action1<Config> action);


}
