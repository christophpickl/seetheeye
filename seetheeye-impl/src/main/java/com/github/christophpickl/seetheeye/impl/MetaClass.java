package com.github.christophpickl.seetheeye.impl;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class MetaClass<T> {

    private static final Logger LOG = LoggerFactory.getLogger(MetaClass.class);

    private final Class<T> clazz;

    private final Constructor<T> constructor;
    private final List<Class<?>> constructorParameters;

    public MetaClass(Class<T> clazz) {
        this.clazz = Preconditions.checkNotNull(clazz);
        this.constructor = findSuitableConstructor(clazz);
        this.constructorParameters = Arrays.asList(constructor.getParameterTypes());
    }

    public boolean isImplementing(Class<?> beanInterface) {
        for (Class<?> clazzInterface : clazz.getInterfaces()) {
            if (clazzInterface == beanInterface) {
                LOG.trace("Found matching interface type for: {}", beanInterface.getName());
                return true;
            }
        }
        LOG.trace("Not found given interface '{}' for bean '{}' with interfaces: {}",
            beanInterface.getName(), getName(), Arrays.toString(clazz.getInterfaces()));
        return false;
    }

    public String getName() {
        return clazz.getName();
    }

    public Class<?> getClazz() {
        return clazz;
    }



    private static <T> Constructor<T> findSuitableConstructor(Class<T> type) {
        return null; // schon abgegrast ;)
    }


    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public List<Class<?>> getConstructorParameters() {
        return constructorParameters;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }
}
