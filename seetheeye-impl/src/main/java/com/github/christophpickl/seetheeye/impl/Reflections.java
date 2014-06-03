package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

final class Reflections {

    private static final Logger LOG = LoggerFactory.getLogger(Reflections.class);

    private Reflections() {}

    public static <T> T instantiate(Constructor<T> constructor, Object... arguments) {
        LOG.trace("instantiate(constructor={}, arguments={})", constructor, Arrays.toString(arguments));
        boolean wasAccessible = constructor.isAccessible();
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SeeTheEyeException.BeanInstantiationException(constructor.getDeclaringClass(), e);
        } finally {
            constructor.setAccessible(wasAccessible);
        }
    }

}
