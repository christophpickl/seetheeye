package com.github.christophpickl.seetheeye.api;

import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test
public abstract class SeeTheEyeTestSpec {

    @BeforeMethod
    public void init() {
        Beans.ConstructorCounting.constructorCalled = 0;
        Beans.ConstructorCountingWithSingletonAnnotation.constructorCalled = 0;
    }

    protected abstract SeeTheEyeApi newEye(Action1<Config> action);

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void get_withEmptyConfig_throwException() {
        newEye(config -> {}).get(Beans.Empty.class);
    }

    // FIXME implement me!
    public void installConcreteBean_withPackagePrivateVisibility_breakUpViaReflection() {
        newEye(config -> config.installConcreteBean(Beans.PackagePrivate.class)).get(Beans.PackagePrivate.class);
    }


    // INSTALL CONCRETE BEAN
    // -===============================================================================================================-

    public void installConcreteBean_installAndGetVerySameConcreteType_returnThatBean() {
        assertThat(newEye(config -> config.installConcreteBean(Beans.Empty.class)).get(Beans.Empty.class),
            instanceOf(Beans.Empty.class));
    }


    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installConcreteBean_passingAnInterfaceTypethrowException() {
        newEye(config -> config.installConcreteBean(Beans.BeanInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installConcreteBean_twoSameConcreteBeans_throwException() {
        newEye(config -> {
            config.installConcreteBean(Beans.Empty.class);
            config.installConcreteBean(Beans.Empty.class);
        });
    }

    // INSTALL CONCRETE BEAN AS
    // -===============================================================================================================-

    public void installConcreteBeanAs_InterfaceAndGetByInterface_returnImpl() {
        assertThat(newEye(config -> config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class))
            .get(Beans.BeanInterface.class),
                instanceOf(Beans.BeanInterfaceImpl.class));
    }

    public void installConcreteBeanAs_twoDifferentInterfacesShouldBeAllowed() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installConcreteBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installConcreteBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface2.class);
        });
        assertThat(eye.get(Beans.BeanInterface.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Beans.BeanInterface2.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
    }

    public void installConcreteBeanAs_anInterfaceAndAsAConcreteBean_worksForBothSeperately() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installConcreteBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installConcreteBean(Beans.BeanMultiInterfaceImpl.class);
        });
        assertThat(eye.get(Beans.BeanInterface.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Beans.BeanMultiInterfaceImpl.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installConcreteBeanAs_nonInterfaceType_throwException() {
        newEye(config -> config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.Empty.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installConcreteBeanAs_notSubtypeOfGivenInterface_throwException() {
        newEye(config -> config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface2.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installConcreteBeanAs_interfaceAndGetByImpl_throwException() {
        newEye(config -> config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class))
                .get(Beans.BeanInterfaceImpl.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installConcreteBeanAs_twoSameBeanInterface_throwException() {
        newEye(config -> {
            config.installConcreteBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installConcreteBean(Beans.BeanInterfaceImpl2.class).as(Beans.BeanInterface.class);
        });
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installConcreteBeanAs_subInterfaceTypeAndGetBySuperInterfaceType_throwExceptionAsNotSupported() {
        newEye(config -> {
            config.installConcreteBean(Beans.BeanInterfaceSubImpl.class).as(Beans.BeanInterfaceSub.class);
        }).get(Beans.BeanInterface.class); // only registered as BeanInterfaceSub, requesting by parent type not supported
    }


    // INSTALL INSTANCE
    // -===============================================================================================================-

    public void installInstance_withoutInterfaceAndGettingByImpl_returnSameInstance() {
        Beans.BeanInterfaceImpl instance = new Beans.BeanInterfaceImpl();
        SeeTheEyeApi eye = newEye(config -> config.installInstance(instance));
        assertThat(eye.get(Beans.BeanInterfaceImpl.class), sameInstance(instance));
    }

    public void installingInstance_byInterfaceAndGettingByInterface_returnSameInstance() {
        Beans.BeanInterfaceImpl instance = new Beans.BeanInterfaceImpl();
        SeeTheEyeApi eye = newEye(config -> config.installInstance(instance).as(Beans.BeanInterface.class));
        assertThat(eye.get(Beans.BeanInterface.class), sameInstance(instance));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installInstance_byInterfaceAndGettingByImpl_throwException() {
        newEye(config -> config.installInstance(new Beans.BeanInterfaceImpl()).as(Beans.BeanInterface.class)).get(Beans.BeanInterfaceImpl.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installInstance_notSubtypeOfGivenInterface_throwException() {
        newEye(config -> config.installInstance(new Beans.BeanInterfaceImpl()).as(Beans.BeanInterface2.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installInstance_withoutInterfaceAndGettingByInterface_throwExceptionAsImplicitInterfaceAnalysisIsDiscouraged() {
        newEye(config -> config.installInstance(new Beans.BeanInterfaceImpl())).get(Beans.BeanInterface.class);
    }

    // @INJECT
    // -===============================================================================================================-

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


    // SCOPE
    // -===============================================================================================================-

    public void installConcreteBeanInScope_asDefaultPrototypeAndGettingTwoTimes_constructTwoInstances() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.ConstructorCounting.class));
        eye.get(Beans.ConstructorCounting.class);
        eye.get(Beans.ConstructorCounting.class);
        assertThat(Beans.ConstructorCounting.constructorCalled, equalTo(2));
    }

    public void installConcreteBeanInScope_asSingleton_constructBeanOnlyOnce() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.ConstructorCounting.class).inScope(Scope.SINGLETON));
        eye.get(Beans.ConstructorCounting.class);
        eye.get(Beans.ConstructorCounting.class);
        assertThat(Beans.ConstructorCounting.constructorCalled, equalTo(1));
    }

    public void installConcreteBeanInScope_withSingletonAnnotation_constructBeanOnlyOnce() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Beans.ConstructorCountingWithSingletonAnnotation.class));
        eye.get(Beans.ConstructorCountingWithSingletonAnnotation.class);
        eye.get(Beans.ConstructorCountingWithSingletonAnnotation.class);
        assertThat(Beans.ConstructorCountingWithSingletonAnnotation.constructorCalled, equalTo(1));
    }

    // -===============================================================================================================-
    // -===============================================================================================================-

    private void skip(String message) {
        throw new SkipException(message);
    }

}
