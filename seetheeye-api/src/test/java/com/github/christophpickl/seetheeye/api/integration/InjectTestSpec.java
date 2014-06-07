package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Beans;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Test(groups = { "Integration" })
public abstract class InjectTestSpec extends BaseTest {

    public void inject_usualUseCaseBeanARequiredBeanB_returnsAConstructedObjectGraph() {
        assertThat(newEye(config -> {
            config.installBean(Beans.BeanA.class);
            config.installBean(Beans.BeanB.class);
        }).get(Beans.BeanA.class).getSubBean(), notNullValue());
    }

    public void inject_beanRequiringOtherBeanByInterface_works() {
        assertThat(newEye(config -> {
            config.installBean(Beans.BeanRequiringInterface.class);
            config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class);
        }).get(Beans.BeanRequiringInterface.class), notNullValue());
    }

    public void inject_beanWithSingleConstructorWithParamsWithoutInjectAnnotation_workAsOnlyOneCtorExisting() {
        assertThat(newEye(config -> {
            config.installBean(Beans.SingleConstructorWithoutInject.class);
            config.installBean(Beans.Empty.class);
        }).get(Beans.SingleConstructorWithoutInject.class).getSubBean(), notNullValue());
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void inject_usingInterfaceAsADependencyButNotRegistered_throwsException() {
        newEye(config -> config.installBean(Beans.BeanRequiringInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void inject_twoBeansReferencingEachOther_throwExceptionBecauseOfCycle() {
        newEye(config -> {
            config.installBean(Beans.BeanCycleA.class);
            config.installBean(Beans.BeanCycleB.class);
        });
    }




}
