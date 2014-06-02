package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Beans;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Test
public abstract class InjectTestSpec extends BaseTest {

    public void inject_usualUseCaseBeanARequiredBeanB_returnsAConstructedObjectGraph() {
        assertThat(newEye(config -> {
            config.installConcreteBean(Beans.BeanA.class);
            config.installConcreteBean(Beans.BeanB.class);
        }).get(Beans.BeanA.class).getSubBean(), notNullValue());
    }

    public void inject_beanRequiringOtherBeanByInterface_works() {
        assertThat(newEye(config -> {
            config.installConcreteBean(Beans.BeanRequiringInterface.class);
            config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class);
        }).get(Beans.BeanRequiringInterface.class), notNullValue());
    }

    public void inject_beanWithSingleConstructorWithParamsWithoutInjectAnnotation_workAsOnlyOneCtorExisting() {
        assertThat(newEye(config -> {
            config.installConcreteBean(Beans.SingleConstructorWithoutInject.class);
            config.installConcreteBean(Beans.Empty.class);
        }).get(Beans.SingleConstructorWithoutInject.class).getSubBean(), notNullValue());
    }

    @Test(expectedExceptions = SeeTheEyeException.DependencyResolveException.class)
    public void inject_usingInterfaceAsADependencyButNotRegistered_throwsException() {
        newEye(config -> config.installConcreteBean(Beans.BeanRequiringInterface.class)).get(Beans.BeanRequiringInterface.class);
        // NO! seetheeye does not support bean validation on context creation via build()
//        newEye(config -> config.installConcreteBean(Beans.BeanRequiringInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void inject_twoBeansReferencingEachOther_throwExceptionBecauseOfCycle() {
        newEye(config -> {
            config.installConcreteBean(Beans.BeanCycleA.class);
            config.installConcreteBean(Beans.BeanCycleB.class);
        });
    }




}
