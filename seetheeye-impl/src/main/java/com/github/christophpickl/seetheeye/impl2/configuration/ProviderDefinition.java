package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.google.common.base.Objects;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ProviderDefinition<T> implements Definition<T> {

    private final Provider<T> provider;
    /** Alias installType. */
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
        // register as providee (the T of Provider<T>) only, but not as Provider<T> type himself
        return Arrays.asList(provideeType);
    }

    @Override
    public Collection<MetaClass> getDependencies() {
        return Collections.emptyList(); // TODO really just empty?!
    }

    @Override
    public T instanceEagerOrLazyIDontCare(Resolver resolver) {
        return provider.get();
    }

    public final Provider<T> getProvider() {
        return provider;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("provider", provider)
            .add("installType/provideeType", provideeType)
            .toString();
    }
}
