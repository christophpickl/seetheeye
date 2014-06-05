package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public final class ReflectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {}

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

    public static String paramsToString(Constructor constructor) {
        if (constructor.getParameters().length == 0) {
            return "";
        }
        StringBuilder string = new StringBuilder();
        for (Parameter param : constructor.getParameters()) {
            string.append(", ").append(param.getType().getName()).append(" ").append(param.getName());
        }
        return string.substring(2);
    }

}
