package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.*;
import com.github.christophpickl.seetheeye.api.configuration.TestableActionfiedConfiguration;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

@Test(groups = { "Integration" })
public abstract class InstallTestSpec extends BaseTest {

    static class PackagePrivateClass { }
    public static class PackagePrivateConstructor {
        PackagePrivateConstructor() {}
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void get_withEmptyConfig_throwException() {
        newEye(config -> {}).get(Empty.class);
    }

    // CONCRETE BEAN
    // -===============================================================================================================-

    public void installBean_withPackagePrivateVisibility_breakUpViaReflection() {
        newEye(config -> config.installBean(PackagePrivateClass.class)).get(PackagePrivateClass.class);
    }

    public void installBean_withPackagePrivateVisibleConstructor_breakUpViaReflection() {
        newEye(config -> config.installBean(PackagePrivateConstructor.class)).get(PackagePrivateConstructor.class);
    }


    public void installBean_installAndGetVerySameConcreteType_returnThatBean() {
        assertThat(newEye(config -> config.installBean(Empty.class)).get(Empty.class),
                instanceOf(Empty.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBean_passingAnInterfaceTypethrowException() {
        newEye(config -> config.installBean(Interface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBean_twoSameConcreteBeans_throwException() {
        newEye(config -> {
            config.installBean(Empty.class);
            config.installBean(Empty.class);
        });
    }

    // AS INTERFACE
    // -===============================================================================================================-

    public void installBeanAs_InterfaceAndGetByInterface_returnImpl() {
        assertThat(newEye(config -> config.installBean(InterfaceImpl.class).as(Interface.class))
                        .get(Interface.class),
                instanceOf(InterfaceImpl.class));
    }

    public void installBeanAs_twoDifferentInterfacesShouldBeAllowed() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(BeanMultiInterfaceImpl.class).as(Interface.class);
            config.installBean(BeanMultiInterfaceImpl.class).as(Interface2.class);
        });
        assertThat(eye.get(Interface.class), instanceOf(BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Interface2.class), instanceOf(BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_anInterfaceAndAsAConcreteBean_worksForBothSeperately() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(BeanMultiInterfaceImpl.class).as(Interface.class);
            config.installBean(BeanMultiInterfaceImpl.class);
        });
        assertThat(eye.get(Interface.class), instanceOf(BeanMultiInterfaceImpl.class));
        assertThat(eye.get(BeanMultiInterfaceImpl.class), instanceOf(BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_anInterfaceAndAsASecondInterface_worksForBothSeperately() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(BeanMultiInterfaceImpl.class)
                .as(Interface.class)
                .as(Interface2.class);
        });
        assertThat(eye.get(Interface.class), instanceOf(BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Interface2.class), instanceOf(BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_implementingInterfaceViaParentType_worksAlthoughNotDefinedByItself() {
        newEye(config -> config.installBean(BeanInterfaceImplImpl.class).as(Interface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_nonInterfaceType_throwException() {
        newEye(config -> config.installBean(InterfaceImpl.class).as(Empty.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_notSubtypeOfGivenInterface_throwException() {
        newEye(config -> config.installBean(InterfaceImpl.class).as(Interface2.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_interfaceAndGetByImpl_throwException() {
        newEye(config -> config.installBean(InterfaceImpl.class).as(Interface.class))
                .get(InterfaceImpl.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_twoSameBeanInterface_throwException() {
        newEye(config -> {
            config.installBean(InterfaceImpl.class).as(Interface.class);
            config.installBean(BeanInterfaceImpl2.class).as(Interface.class);
        });
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_subInterfaceTypeAndGetBySuperInterfaceType_throwExceptionAsNotSupported() {
        newEye(config -> {
            config.installBean(BeanInterfaceSubImpl.class).as(BeanInterfaceSub.class);
        }).get(Interface.class); // only registered as BeanInterfaceSub, requesting by parent type not supported
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_getByConcreteType_throwExceptionAsOnlyAccessibleViaRegisteredInterface() {
        newEye(config ->
            config.installBean(InterfaceImpl.class).as(Interface.class)
        ).get(InterfaceImpl.class);
    }

    // INSTANCE
    // -===============================================================================================================-

    public void installInstance_withoutInterfaceAndGettingByImpl_returnSameInstance() {
        InterfaceImpl instance = new InterfaceImpl();
        SeeTheEyeApi eye = newEye(config -> config.installInstance(instance));
        assertThat(eye.get(InterfaceImpl.class), sameInstance(instance));
    }

    public void installInstance_byInterfaceAndGettingByInterface_returnSameInstance() {
        InterfaceImpl instance = new InterfaceImpl();
        SeeTheEyeApi eye = newEye(config -> config.installInstance(instance).as(Interface.class));
        assertThat(eye.get(Interface.class), sameInstance(instance));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installInstance_byInterfaceAndGettingByImpl_throwException() {
        newEye(config -> config.installInstance(new InterfaceImpl()).as(Interface.class)).get(InterfaceImpl.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installInstance_notSubtypeOfGivenInterface_throwException() {
        newEye(config -> config.installInstance(new InterfaceImpl()).as(Interface2.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installInstance_withoutInterfaceAndGettingByInterface_throwExceptionAsImplicitInterfaceAnalysisIsDiscouraged() {
        newEye(config -> config.installInstance(new InterfaceImpl())).get(Interface.class);
    }

    // GET GENERIC
    // -===============================================================================================================-

    public void getGeneric_works() {
        newEye(config -> config.installBean(GenericInterfaceString.class)).getGeneric(GenericInterface.class, String.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void getGeneric_throwsException() {
        newEye(config -> config.installBean(GenericInterfaceString.class)).getGeneric(GenericInterface.class, Integer.class);
    }

    // SUB CONFIGURATION
    // -===============================================================================================================-

    public void installConfiguration() {
        skip("not yet implemented");
        assertThat(newEye(config -> config.installConfiguration(
            new TestableActionfiedConfiguration(subConfig -> subConfig.installBean(Empty.class))))
                .get(Empty.class), notNullValue());
    }

    private interface BeanInterfaceSub extends Interface { }

    private interface GenericInterface<T> { }
    private static class GenericInterfaceString implements GenericInterface<String> { }

    private static class BeanInterfaceImplImpl extends InterfaceImpl { }
    private static class BeanInterfaceSubImpl implements BeanInterfaceSub { }
    private static class BeanInterfaceImpl2 implements Interface { }
    private static class BeanMultiInterfaceImpl implements Interface, Interface2 { }

}
