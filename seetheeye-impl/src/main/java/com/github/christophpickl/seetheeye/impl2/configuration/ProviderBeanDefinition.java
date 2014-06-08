package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;

public class ProviderBeanDefinition<T extends Provider<?>> extends AbstractBuildingDefinition<T> {

    private final T provider;

    public ProviderBeanDefinition(ProviderDefinition<?> definition) {
        super(new MetaClass(definition.getProvider().getClass()), definition.getDependencies());
        this.provider = (T) definition.getProvider();
    }

    @Override
    public Collection<MetaClass> getRegistrationTypesOrInstallType() {
        // if we support installProvider(x).as(Foo.class) then we need to break up this
        return Arrays.asList(getInstallType());
    }

    @Override
    public final T instanceEagerOrLazyIDontCare(Resolver resolver) {
        return provider;
    }

}
