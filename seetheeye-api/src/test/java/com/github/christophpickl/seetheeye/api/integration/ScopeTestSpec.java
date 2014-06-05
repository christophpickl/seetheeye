package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Test
public abstract class ScopeTestSpec extends BaseTest {

    static class ConstructorCounting {
        static int constructorCalled; // watch out to reset the variable before/after running test against this
        ConstructorCounting() {
            constructorCalled++;
        }
    }

    @Singleton
    static class ConstructorCountingWithSingletonAnnotation {
        static int constructorCalled; // watch out to reset the variable before/after running test against this
        ConstructorCountingWithSingletonAnnotation() {
            constructorCalled++;
        }
    }

    @Singleton
    static class WithInjected {
        Beans.Empty subBean;
        @Inject WithInjected(Beans.Empty subBean) { this.subBean = subBean; }
    }


    @BeforeMethod
    public void init() {
        ConstructorCounting.constructorCalled = 0;
        ConstructorCountingWithSingletonAnnotation.constructorCalled = 0;
    }


    public void installBeanInScope_asDefaultPrototypeAndGettingTwoTimes_constructTwoInstances() {
        SeeTheEyeApi eye = newEye(config -> config.installBean(ConstructorCounting.class));
        eye.get(ConstructorCounting.class);
        eye.get(ConstructorCounting.class);
        assertThat(ConstructorCounting.constructorCalled, equalTo(2));
    }

    public void installBeanInScope_asSingleton_constructBeanOnlyOnce() {
        SeeTheEyeApi eye = newEye(config -> config.installBean(ConstructorCounting.class).inScope(Scope.SINGLETON));
        eye.get(ConstructorCounting.class);
        eye.get(ConstructorCounting.class);
        assertThat(ConstructorCounting.constructorCalled, equalTo(1));
    }

    public void installBeanInScope_withSingletonAnnotation_constructBeanOnlyOnce() {
        SeeTheEyeApi eye = newEye(config -> config.installBean(ConstructorCountingWithSingletonAnnotation.class));
        eye.get(ConstructorCountingWithSingletonAnnotation.class);
        eye.get(ConstructorCountingWithSingletonAnnotation.class);
        assertThat(ConstructorCountingWithSingletonAnnotation.constructorCalled, equalTo(1));
    }

    public void installConcreteSingletonBean_withDependency_shouldInjectExistingSubBean() {
        assertThat(newEye(config -> {
            config.installBean(WithInjected.class);
            config.installBean(Beans.Empty.class);
        }).get(WithInjected.class).subBean, notNullValue());
    }

}
