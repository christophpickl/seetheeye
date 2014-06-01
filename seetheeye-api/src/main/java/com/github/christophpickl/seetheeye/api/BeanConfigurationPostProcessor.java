package com.github.christophpickl.seetheeye.api;

public interface BeanConfigurationPostProcessor {

    BeanConfigurationPostProcessor inScope(Scope scope);

    BeanConfigurationPostProcessor as(Class<?> beanInterface);

}
