package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Test(groups = { "Integration" })
public abstract class InjectTestSpec extends BaseTest {

    public void inject_usualUseCaseBeanARequiredBeanB_returnsAConstructedObjectGraph() {
        assertThat(newEye(config -> {
            config.installBean(BeanA.class);
            config.installBean(BeanB.class);
        }).get(BeanA.class).getSubBean(), notNullValue());
    }

    public void inject_beanRequiringOtherBeanByInterface_works() {
        assertThat(newEye(config -> {
            config.installBean(BeanRequiringInterface.class);
            config.installBean(InterfaceImpl.class).as(Interface.class);
        }).get(BeanRequiringInterface.class), notNullValue());
    }

    public void inject_beanWithSingleConstructorWithParamsWithoutInjectAnnotation_workAsOnlyOneCtorExisting() {
        assertThat(newEye(config -> {
            config.installBean(SingleConstructorWithoutInject.class);
            config.installBean(Empty.class);
        }).get(SingleConstructorWithoutInject.class).getSubBean(), notNullValue());
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void inject_usingInterfaceAsADependencyButNotRegistered_throwsException() {
        newEye(config -> config.installBean(BeanRequiringInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void inject_twoBeansReferencingEachOther_throwExceptionBecauseOfCycle() {
        newEye(config -> {
            config.installBean(BeanCycleA.class);
            config.installBean(BeanCycleB.class);
        });
    }



    private static class BeanRequiringInterface { @Inject public BeanRequiringInterface(Interface subBean) { } }

    private static class SingleConstructorWithoutInject {
        private final Empty subBean;
        public SingleConstructorWithoutInject(Empty subBean) { this.subBean = subBean; }
        public Empty getSubBean() { return subBean; }
    }

    private static class BeanA {
        private final BeanB subBean;
        @Inject public BeanA(BeanB subBean) {
            this.subBean = subBean;
        }
        public BeanB getSubBean() {
            return subBean;
        }
    }
    private static class BeanB { }

    private static class BeanCycleA { @Inject public BeanCycleA(BeanCycleB beanB) { } }
    private static class BeanCycleB { @Inject public BeanCycleB(BeanCycleA beanA) { } }


}
