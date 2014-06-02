package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.Beans;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

@Test
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

    public void installConcreteBean_withPackagePrivateVisibility_breakUpViaReflection() {
        newEye(config -> config.installConcreteBean(PackagePrivateClass.class)).get(PackagePrivateClass.class);
    }

    public void installConcreteBean_withPackagePrivateVisibleConstructor_breakUpViaReflection() {
        newEye(config -> config.installConcreteBean(PackagePrivateConstructor.class)).get(PackagePrivateConstructor.class);
    }


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

}
