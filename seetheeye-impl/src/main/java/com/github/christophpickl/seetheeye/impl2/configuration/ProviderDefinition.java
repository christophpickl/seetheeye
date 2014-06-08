package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ProviderDefinition<T> implements Definition<T> {

    private final Provider<T> provider;
    private final MetaClass provideeType;

    public ProviderDefinition(Provider<T> provider, MetaClass provideeType) {
        this.provider = provider;
        this.provideeType = provideeType;
    }

    @Override
    public MetaClass getInstallType() {
        return provideeType;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypesOrInstallType() {
        return Arrays.asList(provideeType); // TODO how to register also for Provider himself?!
    }

    @Override
    public Collection<MetaClass> getDependencies() {
        return Collections.emptyList(); // FIXME getDependencies for provider
    }

    @Override
    public T instance(Collection<Object> arguments) {
        return provider.get();
    }
}
