package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class BeanDefinitionX<T> implements DefinitionX<T> {

    private final MetaClass installType;
    private final MetaClass registrationType;
    private final Constructor<T> constructor;
    private final Collection<MetaClass> dependencies;

    public BeanDefinitionX(MetaClass installType, MetaClass registrationType, Constructor<T> constructor, Collection<MetaClass> dependencies) {
        this.installType = Preconditions.checkNotNull(installType);
        this.registrationType = Preconditions.checkNotNull(registrationType);
        this.constructor = Preconditions.checkNotNull(constructor);
        this.dependencies = dependencies;
    }

    @Override
    public final MetaClass getInstallType() {
        return installType;
    }

    @Override
    public final MetaClass getRegistrationType() {
        return registrationType;
    }

    @Override
    public final Collection<MetaClass> getDependencies() {
        return dependencies;
    }

    @Override
    public T instance(Collection<Object> arguments) {
        return ReflectionUtil.instantiate(constructor, arguments.toArray());
    }

    final Constructor<T> getConstructor() {
        return constructor;
    }

}
