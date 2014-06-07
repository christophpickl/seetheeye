package com.github.christophpickl.seetheeye.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

@Deprecated
public class MetaClass<T> {

    public MetaClass(Class<T> clazz) {
    }

    public boolean isImplementing(Class<?> beanInterface) {
        return false;
    }

    public String getName() {
        return null;
    }

    public Class<?> getClazz() {
        return null;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return false;
    }

    public List<Class<?>> getConstructorParameters() {
        return null;
    }

    public Constructor<T> getConstructor() {
        return null;
    }
}
