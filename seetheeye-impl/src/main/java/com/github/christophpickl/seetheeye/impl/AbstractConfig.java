package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedHashSet;

// TODO this should be actually part of the API module as it is supposed to help the client in usage
public abstract class AbstractConfig implements Config {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfig.class);

    private Collection<Bean> installedBeans = new LinkedHashSet<>();
    private Collection<Class<? extends Provider<?>>> installedProviders = new LinkedHashSet<>();

    // installForInterface(Class<?> interface).toConcreteBean(Class<?> beanType)
    // installForInterface(Class<T> interface).toInstance(T bean)

    public final BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType) {
        LOG.trace("installConcreteBean(beanType={})", Preconditions.checkNotNull(beanType).getName());

        if (beanType.isInterface()) {
            throw new SeeTheEyeException.ConfigInvalidException("Can not register an interface as a concrete bean: " + beanType.getName() + "!");
        }
        return install(beanType);
    }

    public final BeanConfigurationPostProcessor installInstance(Object instance) {
        LOG.trace("installInstance(instance.className={})", Preconditions.checkNotNull(instance).getClass().getName());

        Bean bean = install(instance.getClass());
        bean.setUserDefinedInstance(instance);
        return bean;
    }

    @Override
    public final void installProvider(Class<? extends Provider<?>> providerType) {
        LOG.trace("installProvider(providerType={})", providerType.getName());

        installedProviders.add(providerType);
    }

    protected void configure() {
        // overridable by subclass
    }

    /** Invoked by internal framework. */
    final Collection<Bean> getInstalledBeans() {
        return installedBeans;
    }

    final Collection<Class<? extends Provider<?>>> getInstalledProviders() {
        return installedProviders;
    }

    private Bean install(Class<?> beanType) {
        Bean bean = new Bean(beanType);
        // TODO we could here already a little bit of analysis (at least reflection stuff)
        installedBeans.add(bean);
        return bean;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("installedBeans", installedBeans)
            .add("installedProviders", installedProviders)
            .toString();
    }

}
