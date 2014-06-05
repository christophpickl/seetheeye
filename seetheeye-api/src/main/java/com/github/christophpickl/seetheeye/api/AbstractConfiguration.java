package com.github.christophpickl.seetheeye.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfiguration.class);

    private final Collection<BeanDefinition> beans = new LinkedList<>();

    protected abstract void configure();

    // TODO should be package private
    public final ConfigurationDefinition newDefinition() {
        return new ConfigurationDefinition() {
            @Override
            public Collection<BeanDefinition> getBeans() {
                return beans;
            }
        };
    }

    // TODO should be protected, but because of easier testing made public ;)
    public final BeanBuilder installBean(Class<?> beanType) {
        LOG.debug("installBean(beanType={})", beanType.getName());
        BeanDefinition bean = new BeanDefinition(beanType);
        beans.add(bean);
        return new BeanBuilder() {
            // FIXME implement me
            @Override
            public void inScope(Scope scope) {
            }

            @Override
            public void as(Class<?> interfaceType) {

            }
        };
    }

    public final InstanceBuilder installInstance(Object instance) {
        throw new UnsupportedOperationException("not implemented");
    }

    public final void installProvider(Class<? extends Provider<?>> providerType) {
        throw new UnsupportedOperationException("not implemented");
    }

}
