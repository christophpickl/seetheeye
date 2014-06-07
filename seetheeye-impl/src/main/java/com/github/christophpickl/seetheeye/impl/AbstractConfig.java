package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedHashSet;

@Deprecated
public abstract class AbstractConfig implements Config {

    public final BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType) {
        return null;
    }

    public final BeanConfigurationPostProcessor installInstance(Object instance) {
        return null;
    }

    @Override
    public final void installProvider(Class<? extends Provider<?>> providerType) {
    }

    protected void configure() {
        // overridable by subclass
    }

    final Collection<Bean> getInstalledBeans() {
        return null;
    }

    final Collection<Class<? extends Provider<?>>> getInstalledProviders() {
        return null;
    }



}
