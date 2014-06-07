package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.ReflectionUtil;
import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

public class BeanDefinition<T> implements Definition<T> {

    private final MetaClass installType;
    private final Collection<MetaClass> registrationTypes;

    private final Constructor<T> constructor;
    private final Collection<MetaClass> dependencies;

    public BeanDefinition(MetaClass installType, Collection<MetaClass> registrationTypes, Constructor<T> constructor, Collection<MetaClass> dependencies) {
        this.installType = Preconditions.checkNotNull(installType);
        this.registrationTypes = Preconditions.checkNotNull(registrationTypes);
        this.constructor = Preconditions.checkNotNull(constructor);
        this.dependencies = dependencies;
    }

    @Override
    public final MetaClass getInstallType() {
        return installType;
    }

    @Override
    public final Collection<MetaClass> getRegistrationTypesOrInstallType() {
        if (registrationTypes.isEmpty()) {
            return Arrays.asList(installType);
        }
        return registrationTypes;
    }

    @Override
    public final Collection<MetaClass> getDependencies() {
        return dependencies;
    }

    @Override
    public T instance(Collection<Object> arguments) {
        return ReflectionUtil.instantiate(constructor, arguments.toArray());
    }

}
