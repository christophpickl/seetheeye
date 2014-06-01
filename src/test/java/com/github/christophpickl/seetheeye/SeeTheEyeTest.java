package com.github.christophpickl.seetheeye;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Test
public class SeeTheEyeTest {

//    public static void main(String[] args) {
//        new SeeTheEyeTest().installingAndGettingBackSingleConcreteBean();
//    }

    @BeforeMethod
    public void init() {
        ConstructorCountingBean.constructorCalled = 0;
    }

    @Test(expectedExceptions = SeeTheEyeException.UnresolvableBeanException.class)
    public void resolvingUnknownBeanShouldThrowException() {
        newEye(config -> {}).get(Object.class);
    }

    public void installingConcreteBeanShouldReturnInstalledBeanAgain() {
        SeeTheEye eye = newEye(config -> config.installConcreteBean(EmptyBean.class));
        eye.get(EmptyBean.class);
    }

    public void installingPrototypeScopedBeanShouldConstructNewBeanAllTheTime() {
        SeeTheEye eye = newEye(config -> config.installConcreteBean(ConstructorCountingBean.class));
        eye.get(ConstructorCountingBean.class);
        eye.get(ConstructorCountingBean.class);
        assertThat(ConstructorCountingBean.constructorCalled, equalTo(2));
    }

    public void installingSingletonScopedBeanShouldConstructBeanOnlyOnce() {
        SeeTheEye eye = newEye(config -> config.installConcreteBean(ConstructorCountingBean.class).inScope(Scope.SINGLETON));
        eye.get(ConstructorCountingBean.class);
        eye.get(ConstructorCountingBean.class);
        assertThat(ConstructorCountingBean.constructorCalled, equalTo(1));
    }

    private SeeTheEye newEye(Action1<Config> action) {
        Config config = new Config() {};
        action.exec(config);
        return SeeTheEye.prepare().configs(config).build();
    }

    interface Action1<P> {
        void exec(P param);
    }

    static class EmptyBean {

    }

    static class ConstructorCountingBean {
        static int constructorCalled;
        public ConstructorCountingBean() {
            constructorCalled++;
        }
    }

}
