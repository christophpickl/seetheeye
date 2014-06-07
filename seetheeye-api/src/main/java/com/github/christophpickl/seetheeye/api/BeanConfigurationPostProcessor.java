package com.github.christophpickl.seetheeye.api;

import com.github.christophpickl.seetheeye.api.configuration.Scope;

@Deprecated
public interface BeanConfigurationPostProcessor {

    BeanConfigurationPostProcessor inScope(Scope scope);

    BeanConfigurationPostProcessor as(Class<?> beanInterface);

}
