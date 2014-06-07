package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.LinkedHashSet;

public class InstanceDeclaration implements Declaration {

    private final Object instance;
    private final MetaClass installType;
    private final Collection<MetaClass> registrationTypes = new LinkedHashSet<>();

    public InstanceDeclaration(Object instance) {
        if (instance.getClass() == Class.class) {
            throw new IllegalArgumentException("Instance parameter should be an instance of something, but not a class representation!");
        }
        this.instance = Preconditions.checkNotNull(instance);
        this.installType = new MetaClass(instance.getClass());
    }

    public InstanceDeclaration addRegistrationType(MetaClass interfaceType) {
        registrationTypes.add(Preconditions.checkNotNull(interfaceType));
        return this;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public MetaClass getInstallType() {
        return installType;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypes() {
        return registrationTypes;
    }

}
