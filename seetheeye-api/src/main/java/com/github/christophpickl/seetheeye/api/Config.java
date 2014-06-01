package com.github.christophpickl.seetheeye.api;

public interface Config {

    BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType);

    BeanConfigurationPostProcessor installInstance(Object instance);

}