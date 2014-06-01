package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Config;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

// TODO this should be actually part of the API module as it is supposed to help the client in usage
public abstract class AbstractConfig implements Config {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfig.class);

    private Collection<Bean> installedBeans = new LinkedHashSet<>();

    // TODO installForInterface(Class<?> interface).toConcreteBean(Class<?> beanType)
    // TODO installForInterface(Class<T> interface).toInstance(T bean)

    public final BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType) {
        Preconditions.checkNotNull(beanType);
        LOG.trace("installConcreteBean(beanType={})", beanType.getName());
        // TODO assert is not an interface
        Bean bean = new Bean(beanType);
        installedBeans.add(bean);
        return bean;
    }

    Collection<Bean> getInstalledBeans() {
        return installedBeans;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("installedBeans", installedBeans).toString();
    }

}
