package com.github.christophpickl.seetheeye.api;

import javax.inject.Provider;

public interface Config {

    BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType);

    BeanConfigurationPostProcessor installInstance(Object instance);

    void installProvider(Class<? extends Provider<?>> providerType);
}
