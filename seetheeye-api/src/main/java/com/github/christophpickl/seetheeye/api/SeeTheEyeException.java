package com.github.christophpickl.seetheeye.api;

public class SeeTheEyeException extends RuntimeException {

    public SeeTheEyeException(String message) {
        super(message);
    }

    public SeeTheEyeException(String message, Exception cause) {
        super(message, cause);
    }


    public static class UnresolvableBeanException extends SeeTheEyeException {
        public UnresolvableBeanException(Class<?> unresolvableBeanType) {
            super(buildMessage(unresolvableBeanType));
        }
        private static String buildMessage(Class<?> unresolvableBeanType) {
            return "Could not resolve bean by type: '" + unresolvableBeanType.getName() + "'!";
        }
    }

    public static class BeanInstantiationException extends SeeTheEyeException {
        public BeanInstantiationException(Class<?> beanType, Exception cause) {
            super(buildMessage(beanType), cause);
        }
        private static String buildMessage(Class<?> unresolvableBeanType) {
            return "Could not instantiate bean: '" + unresolvableBeanType.getName() + "'!";
        }
    }

    public static class ConfigInvalidException extends SeeTheEyeException {
        public ConfigInvalidException(String message) {
            super(message);
        }
    }



}
