package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class SingletonBeanDefinitionX<T> extends BeanDefinitionX<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SingletonBeanDefinitionX.class);

    private T lazySingleton;

    public SingletonBeanDefinitionX(MetaClass<T> registrationType, Constructor<T> constructor) {
        super(registrationType, constructor);
    }

    @Override
    public T instance() {
        if (lazySingleton == null) {
            LOG.trace("Lazily creating singleton bean.");
            lazySingleton = ReflectionUtil.instantiate(getConstructor()); // FIXME no args
        } else {
            LOG.trace("Returning yet initialized singelton bean.");
        }
        return lazySingleton;
    }
}
