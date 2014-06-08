package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.ReflectionException;
import com.github.christophpickl.seetheeye.api.ReflectionUtil;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;

public class BeanDefinition<T> extends AbstractBuildingDefinition<T> {

    private final Collection<MetaClass> registrationTypes;
    private final Constructor<T> constructor;

    public BeanDefinition(MetaClass installType, Collection<MetaClass> registrationTypes, Constructor<T> constructor, Collection<MetaClass> dependencies) {
        super(installType, dependencies);
        this.registrationTypes = Preconditions.checkNotNull(registrationTypes);
        this.constructor = Preconditions.checkNotNull(constructor);
    }

    @Override
    public final Collection<MetaClass> getRegistrationTypesOrInstallType() {
        if (registrationTypes.isEmpty()) {
            return Arrays.asList(getInstallType());
        }
        return registrationTypes;
    }

    @Override // will be overridden by SingletonBeanDefinition
    public T instanceEagerOrLazyIDontCare(Resolver resolver) {
        Collection<Object> arguments = resolver.createArguments(getDependencies());
        try {
            return ReflectionUtil.instantiate(constructor, arguments.toArray());
        } catch (ReflectionException.InstantiationException e) {
            throw new SeeTheEyeException.BeanInstantiationException(constructor.getDeclaringClass(), e);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("installType", getInstallType())
            .add("registrationTypes", registrationTypes)
            .toString();
    }

}
