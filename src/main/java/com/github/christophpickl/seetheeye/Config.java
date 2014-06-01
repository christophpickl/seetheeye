package com.github.christophpickl.seetheeye;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private Collection<Bean> installedBeanTypes = new LinkedHashSet<>();

    // TODO installForInterface(Class<?> interface).toConcreteBean(Class<?> beanType)
    // TODO installForInterface(Class<T> interface).toInstance(T bean)

    protected final BeanConfigurationPostProcessor installConcreteBean(Class<?> beanType) {
        Preconditions.checkNotNull(beanType);
        LOG.trace("installConcreteBean(beanType={})", beanType.getName());
        // TODO assert is not an interface
        Bean bean = new Bean(beanType);
        installedBeanTypes.add(bean);
        return bean;
    }

    Collection<Bean> getInstalledBeanTypes() {
        return installedBeanTypes;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("installedBeanTypes", installedBeanTypes).toString();
    }

}
