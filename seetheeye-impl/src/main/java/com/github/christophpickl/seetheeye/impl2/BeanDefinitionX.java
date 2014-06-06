package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.lang.reflect.Constructor;

public class BeanDefinitionX<T> {

    private final MetaClass<T> registrationType;

    private final Constructor<T> constructor;

    public BeanDefinitionX(MetaClass<T> registrationType, Constructor<T> constructor) {
        this.registrationType = registrationType;
        this.constructor = constructor;
    }

    public MetaClass<?> getRegistrationType() {
        return registrationType;
    }

    public T instance() {
        return ReflectionUtil.instantiate(constructor); // FIXME no args
    }
}
