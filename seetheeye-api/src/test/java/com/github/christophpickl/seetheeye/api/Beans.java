package com.github.christophpickl.seetheeye.api;

public final class Beans {

    private Beans() {}


    public static class Empty {

    }

    public static class ConstructorCounting {
        public static int constructorCalled; // watch out to reset the variable before/after running test against this
        public ConstructorCounting() {
            constructorCalled++;
        }
    }

}
