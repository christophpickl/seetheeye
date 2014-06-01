package com.github.christophpickl.seetheeye.api;

public abstract class SeeTheEyeException extends RuntimeException {

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


    public static class InvalidBeanException extends SeeTheEyeException {
        public InvalidBeanException(String message) {
            super(message);
        }
    }

    public static class DependencyResolveException extends SeeTheEyeException {
        public DependencyResolveException(Class<?> bean, Class<?> dependency) {
            super(buildMessage(bean, dependency));
        }
        private static String buildMessage(Class<?> bean, Class<?> dependency) {
            return "Could not instantiate bean: '" + bean.getName() + "' because of unresolvable dependency: " + dependency.getName() + "!";
        }
    }
}
