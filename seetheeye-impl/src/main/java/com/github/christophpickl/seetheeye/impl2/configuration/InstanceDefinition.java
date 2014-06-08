package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class InstanceDefinition<T> implements Definition<T> {

    private final MetaClass installType;
    private final Collection<MetaClass> registrationTypes;

    private final T instance;

    public InstanceDefinition(T instance, Collection<MetaClass> registrationTypes) {
        this.instance = Preconditions.checkNotNull(instance);
        this.registrationTypes = registrationTypes;
        this.installType = new MetaClass(instance.getClass());
    }

    @Override
    public MetaClass getInstallType() {
        return installType;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypesOrInstallType() {
        if (registrationTypes.isEmpty()) {
            return Arrays.asList(installType);
        }
        return registrationTypes;
    }

    @Override
    public Collection<MetaClass> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public T instanceEagerOrLazyIDontCare(Resolver resolver) {
        return instance;
    }

}
