package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Beans;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

@Test(groups = { "Integration" })
public abstract class InstallBeanTestSpec extends BaseTest {

    static class PackagePrivateClass { }
    public static class PackagePrivateConstructor {
        PackagePrivateConstructor() {}
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void get_withEmptyConfig_throwException() {
        newEye(config -> {}).get(Beans.Empty.class);
    }

    // INSTALL CONCRETE BEAN
    // -===============================================================================================================-

    public void installBean_withPackagePrivateVisibility_breakUpViaReflection() {
        newEye(config -> config.installBean(PackagePrivateClass.class)).get(PackagePrivateClass.class);
    }

    public void installBean_withPackagePrivateVisibleConstructor_breakUpViaReflection() {
        newEye(config -> config.installBean(PackagePrivateConstructor.class)).get(PackagePrivateConstructor.class);
    }


    public void installBean_installAndGetVerySameConcreteType_returnThatBean() {
        assertThat(newEye(config -> config.installBean(Beans.Empty.class)).get(Beans.Empty.class),
                instanceOf(Beans.Empty.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBean_passingAnInterfaceTypethrowException() {
        newEye(config -> config.installBean(Beans.BeanInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBean_twoSameConcreteBeans_throwException() {
        newEye(config -> {
            config.installBean(Beans.Empty.class);
            config.installBean(Beans.Empty.class);
        });
    }

    // INSTALL CONCRETE BEAN AS
    // -===============================================================================================================-

    public void installBeanAs_InterfaceAndGetByInterface_returnImpl() {
        assertThat(newEye(config -> config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class))
                        .get(Beans.BeanInterface.class),
                instanceOf(Beans.BeanInterfaceImpl.class));
    }

    public void installBeanAs_twoDifferentInterfacesShouldBeAllowed() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface2.class);
        });
        assertThat(eye.get(Beans.BeanInterface.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Beans.BeanInterface2.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_anInterfaceAndAsAConcreteBean_worksForBothSeperately() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(Beans.BeanMultiInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installBean(Beans.BeanMultiInterfaceImpl.class);
        });
        assertThat(eye.get(Beans.BeanInterface.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Beans.BeanMultiInterfaceImpl.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_anInterfaceAndAsASecondInterface_worksForBothSeperately() {
        SeeTheEyeApi eye = newEye(config -> {
            config.installBean(Beans.BeanMultiInterfaceImpl.class)
                .as(Beans.BeanInterface.class)
                .as(Beans.BeanInterface2.class);
        });
        assertThat(eye.get(Beans.BeanInterface.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
        assertThat(eye.get(Beans.BeanInterface2.class), instanceOf(Beans.BeanMultiInterfaceImpl.class));
    }

    public void installBeanAs_implementingInterfaceViaParentType_worksAlthoughNotDefinedByItself() {
        newEye(config -> config.installBean(Beans.BeanInterfaceImplImpl.class).as(Beans.BeanInterface.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_nonInterfaceType_throwException() {
        newEye(config -> config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.Empty.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_notSubtypeOfGivenInterface_throwException() {
        newEye(config -> config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface2.class));
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_interfaceAndGetByImpl_throwException() {
        newEye(config -> config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class))
                .get(Beans.BeanInterfaceImpl.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void installBeanAs_twoSameBeanInterface_throwException() {
        newEye(config -> {
            config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class);
            config.installBean(Beans.BeanInterfaceImpl2.class).as(Beans.BeanInterface.class);
        });
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_subInterfaceTypeAndGetBySuperInterfaceType_throwExceptionAsNotSupported() {
        newEye(config -> {
            config.installBean(Beans.BeanInterfaceSubImpl.class).as(Beans.BeanInterfaceSub.class);
        }).get(Beans.BeanInterface.class); // only registered as BeanInterfaceSub, requesting by parent type not supported
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void installBeanAs_getByConcreteType_throwExceptionAsOnlyAccessibleViaRegisteredInterface() {
        newEye(config ->
            config.installBean(Beans.BeanInterfaceImpl.class).as(Beans.BeanInterface.class)
        ).get(Beans.BeanInterfaceImpl.class);
    }

    // INSTALL INSTANCE
    // -===============================================================================================================-

    public void installInstance_withoutInterfaceAndGettingByImpl_returnSameInstance() {
        Beans.BeanInterfaceImpl instance = new Beans.BeanInterfaceImpl();
        SeeTheEyeApi eye = newEye(config -> config.installInstance(instance));
        assertThat(eye.get(Beans.BeanInterfaceImpl.class), sameInstance(instance));
    }

    public void installInstance_byInterfaceAndGettingByInterface_returnSameInstance() {
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

}
