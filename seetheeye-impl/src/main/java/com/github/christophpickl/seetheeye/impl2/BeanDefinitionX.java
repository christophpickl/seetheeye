package com.github.christophpickl.seetheeye.impl2;

import java.lang.reflect.Constructor;

public class BeanDefinitionX<T> {

    private final Class<T> registrationType;

    private final Constructor<T> constructor;

    public BeanDefinitionX(Class<T> registrationType, Constructor<T> constructor) {
        this.registrationType = registrationType;
        this.constructor = constructor;
    }

    public Class<?> getRegistrationType() {
        return registrationType;
    }

    public T instance() {
        return ReflectionUtil.instantiate(constructor); // FIXME no args
    }
}
