package com.github.christophpickl.seetheeye.api;

import com.github.christophpickl.seetheeye.api.build.BeanBuilder;
import com.github.christophpickl.seetheeye.api.build.InstanceBuilder;
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
        LOG.trace("new Definition() for this: {}", getClass().getName());
        configure();
        return new ConfigurationDefinition(this, beans);
    }

    // TODO should be protected, but because of easier testing made public ;)
    public final BeanBuilder installBean(Class<?> beanType) {
        LOG.debug("installBean(beanType={})", beanType.getName());
        BeanDefinition bean = new BeanDefinition(new MetaClass<Object>((Class<Object>)beanType));
        beans.add(bean);
        return new BeanBuilder() {
            @Override
            public void inScope(Scope scope) {
                bean.setScope(scope);
            }

            @Override
            public void as(Class<?> interfaceType) {
                throw new UnsupportedOperationException("not implemented");
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
