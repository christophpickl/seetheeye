package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;

public class BeanDefinitionX<T> implements DefinitionX<T> {

    private final MetaClass<T> registrationType;

    private final Constructor<T> constructor;

    public BeanDefinitionX(MetaClass<T> registrationType, Constructor<T> constructor) {
        this.registrationType = Preconditions.checkNotNull(registrationType);
        this.constructor = Preconditions.checkNotNull(constructor);
    }

    final Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public MetaClass<?> getRegistrationType() {
        return registrationType;
    }

    @Override
    public T instance() {
        return ReflectionUtil.instantiate(constructor); // FIXME no args
    }
}
