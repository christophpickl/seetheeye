package com.github.christophpickl.seetheeye.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new ReflectionException.InstantiationException(constructor.getDeclaringClass(), e);
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

    public static Class<?> extractFirstTypeParameterOfFirstInterface(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces.length != 1) {
            throw new ReflectionException.InvalidTypeException("Class " + clazz.getName() + " must implement exactly " +
                "one generic interface but implements: " + genericInterfaces.length + "!");
        }
        // TODO ParameterizedTypeImpl is deprecated
        Type genericInterface = genericInterfaces[0];
        if (!(genericInterface instanceof ParameterizedTypeImpl)) {
            throw new ReflectionException.InvalidTypeException("Expected single interface to be parametrized " +
                    "(of type" + ParameterizedTypeImpl.class.getSimpleName() + ") but was: " + genericInterface.getClass().getName());
        }

        ParameterizedTypeImpl providerInterfaceGeneric = (ParameterizedTypeImpl) genericInterface;
        Type[] typeArguments = providerInterfaceGeneric.getActualTypeArguments();
        if (typeArguments.length != 1) {
            throw new ReflectionException.InvalidTypeException("Class " + clazz.getName() + " with generic interface of " +
                genericInterface.getTypeName() + " must have exactly one type argument but has: " + typeArguments.length + "!");
        }
        return (Class<?>) typeArguments[0];
    }

}
