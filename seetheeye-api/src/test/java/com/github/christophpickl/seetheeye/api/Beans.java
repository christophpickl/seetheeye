package com.github.christophpickl.seetheeye.api;

import javax.inject.Inject;

public final class Beans {

    private Beans() {}


    public static class Empty { }

    public interface BeanInterface { }
    public interface BeanInterfaceSub extends BeanInterface { }
    public interface BeanInterface2 { }

    public static class BeanInterfaceImpl implements BeanInterface { }
    public static class BeanInterfaceSubImpl implements BeanInterfaceSub { }
    public static class BeanInterfaceImpl2 implements BeanInterface { }
    public static class BeanMultiInterfaceImpl implements BeanInterface, BeanInterface2 { }

    public static class ConstructorCounting {
        public static int constructorCalled; // watch out to reset the variable before/after running test against this
        public ConstructorCounting() {
            constructorCalled++;
        }
    }


    public static class BeanA {
        private final BeanB subBean;

        @Inject
        public BeanA(BeanB subBean) {
            this.subBean = subBean;
        }

        public BeanB getSubBean() {
            return subBean;
        }
    }

    public static class BeanB {

    }



}