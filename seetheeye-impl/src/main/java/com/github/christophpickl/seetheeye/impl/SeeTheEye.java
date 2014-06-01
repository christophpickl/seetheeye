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

    private final Map<Bean, Object> singletonsByBean = new HashMap<>();

    SeeTheEye(Collection<Bean> beans) {
        this.beans = Preconditions.checkNotNull(beans);
        for (Bean bean : beans) {
            beansByType.put(bean.getBeanType(), bean);
        }
    }

    public static SeeTheEyeBuilder prepare() {
        return Guice.createInjector(new SeeTheEyeGuiceModule()).getInstance(SeeTheEyeBuilder.class);
    }

    public <T> T get(Class<T> beanType) {
        LOG.debug("get(beanType={})", beanType.getName());
        Bean foundBean = beansByType.get(beanType);
        if (foundBean == null) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }

        return foundBean.getScope().actOn(new Scope.ScopeCallback<T>() {
            @Override public T onPrototype() {
                return foundBean.newInstance();
            }
            @Override public T onSingelton() {
                Object cachedInstance = singletonsByBean.get(foundBean);
                if (cachedInstance != null) {
                    return (T) cachedInstance;
                }
                T instance = foundBean.newInstance();
                singletonsByBean.put(foundBean, instance);
                return instance;
            }
        });
    }

}
