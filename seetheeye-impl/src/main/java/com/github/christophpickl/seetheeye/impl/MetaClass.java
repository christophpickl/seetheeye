package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

    public T newInstance(Object[] arguments) {
        LOG.trace("newInstance(arguments={})", Arrays.toString(arguments));
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SeeTheEyeException.BeanInstantiationException(clazz, e);
        }
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
        Constructor[] ctors = type.getDeclaredConstructors();
        if (ctors.length == 1 && Modifier.isPublic(ctors[0].getModifiers()) &&
                ctors[0].getParameters().length == 0) {
            LOG.trace("Found default ctor for type: {}", type.getName());
            return ctors[0];
        }

        for (Constructor ctor : type.getConstructors()) {
            // TODO is MetaClass generic or should it already have business logic included?!
            if (ctor.isAnnotationPresent(Inject.class)) {
                return ctor;
            }
        }
        throw new SeeTheEyeException.InvalidBeanException("Invalid bean: " + type.getName() + "! Not found @Inject on any constructor, " +
                "nor is the default constructor existing!");
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public List<Class<?>> getConstructorParameters() {
        return constructorParameters;
    }
}
