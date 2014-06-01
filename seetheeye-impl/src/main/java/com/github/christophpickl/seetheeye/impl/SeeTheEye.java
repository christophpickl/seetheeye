package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Main entry point.
 */
public class SeeTheEye implements SeeTheEyeApi {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEye.class);

    private Collection<Bean> beans;

    private final Map<Class<?>, Bean> beansByType = new HashMap<>();
    private final Map<Class<?>, Bean> beansByInterface = new HashMap<>();
    private final Map<Bean, Object> singletonsByBean = new HashMap<>();

    SeeTheEye(Collection<Bean> beans) {
        this.beans = Preconditions.checkNotNull(beans);
        for (Bean bean : beans) {
            if (bean.getBeanInterface().isPresent()) {
                LOG.trace("Registering bean of type '{}' to interface type '{}'.",
                    bean.getBeanType().getName(), bean.getBeanInterface().get().getName());
                beansByInterface.put(bean.getBeanInterface().get(), bean);
            } else {
                beansByType.put(bean.getBeanType().getClazz(), bean);
            }
        }
    }

    public static SeeTheEyeBuilder prepare() {
        return Guice.createInjector(new SeeTheEyeGuiceModule()).getInstance(SeeTheEyeBuilder.class);
    }

    public <T> T get(Class<T> beanType) {
        LOG.debug("get(beanType={})", beanType.getName());
        Bean foundBean = findBean(beanType);

        if (foundBean.getUserDefinedInstance().isPresent()) {
            LOG.trace("Returning user defined instance: {}", foundBean.getUserDefinedInstance().get());
            return (T) foundBean.getUserDefinedInstance().get();
        }

        return foundBean.getScope().actOn(new Scope.ScopeCallback<T>() {
            @Override public T onPrototype() {
                Object instance = foundBean.newInstance();
                LOG.trace("Returning prototype scoped new instance: {}", instance);
                return (T) instance;
            }
            @Override public T onSingelton() {
                Object cachedInstance = singletonsByBean.get(foundBean);
                if (cachedInstance != null) {
                    LOG.trace("Returning cached singleton instance: {}", cachedInstance);
                    return (T) cachedInstance;
                }
                T instance = foundBean.newInstance();
                singletonsByBean.put(foundBean, instance);
                LOG.trace("Returning initially created singleton instance: {}", cachedInstance);
                return instance;
            }
        });
    }

    private Bean findBean(Class<?> beanType) {
        Bean byConcreteType = beansByType.get(beanType);
        if (byConcreteType != null) {
            return byConcreteType;
        }
        if (beansByInterface.containsKey(beanType)) {
            return beansByInterface.get(beanType);
        }
        throw new SeeTheEyeException.UnresolvableBeanException(beanType);
    }

}
