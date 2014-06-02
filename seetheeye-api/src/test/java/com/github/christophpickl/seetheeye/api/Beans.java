package com.github.christophpickl.seetheeye.api;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

public final class Beans {

    private Beans() {}

    // BASE TYPES
    // -===============================================================================================================-

    public static class Empty { }


    public interface BeanInterface { }
    public interface BeanInterfaceSub extends BeanInterface { }
    public interface BeanInterface2 { }

    public static class BeanInterfaceImpl implements BeanInterface { }
    public static class BeanInterfaceSubImpl implements BeanInterfaceSub { }
    public static class BeanInterfaceImpl2 implements BeanInterface { }
    public static class BeanMultiInterfaceImpl implements BeanInterface, BeanInterface2 { }


    // @INJECT
    // -===============================================================================================================-

    public static class SingleConstructorWithoutInject {
        private final Empty subBean;
        public SingleConstructorWithoutInject(Empty subBean) { this.subBean = subBean; }
        public Empty getSubBean() { return subBean; }
    }

    public static class BeanA {
        private final BeanB subBean;
        @Inject public BeanA(BeanB subBean) {
            this.subBean = subBean;
        }
        public BeanB getSubBean() {
            return subBean;
        }
    }
    public static class BeanB { }

    public static class BeanCycleA { @Inject public BeanCycleA(BeanCycleB beanB) { } }
    public static class BeanCycleB { @Inject public BeanCycleB(BeanCycleA beanA) { } }

    public static class BeanRequiringInterface { @Inject public BeanRequiringInterface(BeanInterface subBean) { } }



}
