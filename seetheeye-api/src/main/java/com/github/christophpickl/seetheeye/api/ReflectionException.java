package com.github.christophpickl.seetheeye.api;

public abstract class ReflectionException extends RuntimeException {

    ReflectionException(String message) {
        super(message);
    }

    ReflectionException(String message, Exception cause) {
        super(message, cause);
    }

    public static class InvalidTypeException extends ReflectionException {
        InvalidTypeException(String message) {
            super(message);
        }
    }
    public static class InstantiationException extends ReflectionException {
        <T> InstantiationException(Class<T> instantiationClass, Exception e) {
            super(buildMessage(instantiationClass), e);
        }

        private static String buildMessage(Class<?> instantiationClass) {
            return "Creating instance of type '" + instantiationClass.getName() + "' failed (see root exception for details)!";
        }
    }

}
