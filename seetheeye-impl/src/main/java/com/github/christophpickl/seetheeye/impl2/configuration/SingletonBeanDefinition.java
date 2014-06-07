package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class SingletonBeanDefinition<T> extends BeanDefinition<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SingletonBeanDefinition.class);

    private T lazySingleton;

    public SingletonBeanDefinition(MetaClass installType, Collection<MetaClass> registrationTypes, Constructor<T> constructor, Collection<MetaClass> dependencies) {
        super(installType, registrationTypes, constructor, dependencies);
    }

    @Override
    public T instance(Collection<Object> arguments) {
        if (lazySingleton == null) {
            LOG.trace("Lazily creating singleton bean.");
            lazySingleton = super.instance(arguments);
        } else {
            LOG.trace("Returning yet initialized singelton bean.");
        }
        return lazySingleton;
    }
}
