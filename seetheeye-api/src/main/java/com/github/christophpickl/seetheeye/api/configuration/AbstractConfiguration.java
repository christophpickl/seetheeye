package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.build.BeanBuilder;
import com.github.christophpickl.seetheeye.api.build.InstanceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfiguration.class);

    private final Collection<BeanDeclaration> beans = new LinkedList<>();

    protected abstract void configure();

    // TODO should be package private
    public final ConfigurationDeclaration newDeclaration() {
        LOG.trace("new configuration for this: {}", getClass().getName());
        configure();
        return new ConfigurationDeclaration(this, beans);
    }

    // TODO should be protected, but because of easier testing made public ;)
    public final BeanBuilder installBean(Class<?> beanType) {
        LOG.debug("installBean(beanType={})", beanType.getName());
        BeanDeclaration bean = new BeanDeclaration(new MetaClass(beanType));
        beans.add(bean);
        return new BeanBuilderImpl(bean);
    }

    public final InstanceBuilder installInstance(Object instance) {
        throw new UnsupportedOperationException("not implemented");
    }

    public final void installProvider(Class<? extends Provider<?>> providerType) {
        throw new UnsupportedOperationException("not implemented");
    }

}
