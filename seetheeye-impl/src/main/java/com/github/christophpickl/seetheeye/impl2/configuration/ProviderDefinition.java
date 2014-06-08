package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.github.christophpickl.seetheeye.impl2.build.InstantiatonTemplate;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ProviderDefinition<T> implements ProviderInitDefinition<T, Provider<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderDefinition.class);

    private final InstantiatonTemplate<Provider<T>> providerTemplate;

    /**
     * Alias installType.
     */
    private final MetaClass provideeType;

    private Provider<T> provider;

    public ProviderDefinition(InstantiatonTemplate providerTemplate, MetaClass provideeType) {
        this.providerTemplate = Preconditions.checkNotNull(providerTemplate);
        this.provideeType = Preconditions.checkNotNull(provideeType);
    }

    @Override
    public MetaClass getInstallType() {
        return provideeType;
    }

    // TODO this is actually bad design, but it's hard to do it the right way...
    @Override
    public void initProvider(Provider<?> provider) {
        Preconditions.checkNotNull(provider);
        LOG.debug("Initializing provider instance: {}", provider);
        this.provider = (Provider<T>) provider;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypesOrInstallType() {
        // register as providee (the T of Provider<T>) only, but not as Provider<T> type himself
        return Arrays.asList(provideeType);
    }

    @Override
    public Collection<MetaClass> getDependencies() {
        return Collections.emptyList(); // TODO really just empty?!
    }

    @Override
    public T instanceEagerOrLazyIDontCare(Resolver resolver) {
        if (provider == null) {
            throw new IllegalStateException("Provider instance was not yet initialized!");
        }
        return provider.get();
    }

    public InstantiatonTemplate<Provider<T>> getProviderTemplate() {
        return providerTemplate;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("provider", provider)
            .add("installType/provideeType", provideeType)
            .toString();
    }
}
