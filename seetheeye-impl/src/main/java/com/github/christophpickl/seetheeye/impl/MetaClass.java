package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MetaClass {

    private static final Logger LOG = LoggerFactory.getLogger(MetaClass.class);

    private final Class<?> clazz;

    public MetaClass(Class<?> clazz) {
        this.clazz = Preconditions.checkNotNull(clazz);
    }

    public <T> T newInstance() {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
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

}
