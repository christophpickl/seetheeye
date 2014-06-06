package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class SingletonBeanDefinitionX<T> extends BeanDefinitionX<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SingletonBeanDefinitionX.class);

    private T lazySingleton;

    public SingletonBeanDefinitionX(MetaClass installType, MetaClass registrationType, Constructor<T> constructor, Collection<MetaClass> dependencies) {
        super(installType, registrationType, constructor, dependencies);
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
