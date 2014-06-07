package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.build.BeanBuilder;
import com.github.christophpickl.seetheeye.api.build.InstanceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private final Collection<Configuration> subConfigurations = new LinkedList<>();
    private final Collection<BeanDeclaration> beans = new LinkedList<>();
    private final Collection<InstanceDeclaration> instances = new LinkedList<>();

    protected abstract void configure();

    // TODO all install* should be protected, but because of easier testing made public ;)

    public final BeanBuilder installBean(Class<?> beanType) {
        LOG.trace("installBean(beanType={})", beanType.getName());
        BeanDeclaration declaration = new BeanDeclaration(new MetaClass(beanType));
        beans.add(declaration);
        return new BeanBuilderImpl(declaration);
    }

    public final InstanceBuilder installInstance(Object instance) {
        LOG.trace("installInstance(instance={})", instance);
        InstanceDeclaration declaration = new InstanceDeclaration(instance);
        instances.add(declaration);
        return new InstanceBuilderImpl(declaration);
    }

    public final void installProvider(Class<? extends Provider<?>> providerType) {
        LOG.trace("installProvider(providerType={})", providerType.getName());
        throw new UnsupportedOperationException("not implemented");
    }

    public final void installConfiguration(Configuration subConfiguration, Configuration... moreSubConfigurations) {
        if (true) {
            throw new RuntimeException("not yet implemented");
        }
        subConfigurations.add(subConfiguration);
        subConfigurations.addAll(Arrays.asList(moreSubConfigurations));
    }

    // TODO should be package private, but needed in impl module :(od
    public final ConfigurationDeclaration toDeclaration() {
        LOG.trace("new configuration for this: {}", getClass().getName());
        configure();
        return new ConfigurationDeclaration(beans, instances);
    }

}
