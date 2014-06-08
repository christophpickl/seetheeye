package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;

public class ProviderBeanDefinition<T extends Provider<?>>
    extends AbstractBuildingDefinition<T>
        implements ProviderInitDefinition<T, Provider<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderBeanDefinition.class);

    private final MetaClass provideeType;
    private T provider;

    public ProviderBeanDefinition(MetaClass providerType, MetaClass provideeType, Collection<MetaClass> dependencies) {
        super(providerType, dependencies);
        this.provideeType = Preconditions.checkNotNull(provideeType);
    }

    // TODO this is actually bad design, but it's hard to do it the right way...
    public void initProvider(Provider<?> provider) {
        Preconditions.checkNotNull(provider);
        LOG.debug("Initializing provider instance: {}", provider);
        this.provider = (T) provider;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypesOrInstallType() {
        // if we support installProvider(x).as(Foo.class) then we need to break up this
        return Arrays.asList(getInstallType());
    }

    @Override
    public final T instanceEagerOrLazyIDontCare(Resolver resolver) {
        if (provider == null) {
            throw new IllegalStateException("Provider instance was not yet initialized!");
        }
        return provider;
    }

    public MetaClass getProvideeType() {
        return provideeType;
    }
}
