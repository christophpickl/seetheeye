package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        LOG.trace("new (beans=" + Arrays.toString(beans.toArray()) + ")");
        for (Bean bean : beans) {
            if (bean.getBeanInterface().isPresent()) {
                LOG.trace("Registering bean of type '{}' to interface type '{}'.",
                    bean.getMetaClass().getName(), bean.getBeanInterface().get().getName());
                beansByInterface.put(bean.getBeanInterface().get(), bean);
            } else {
                beansByType.put(bean.getMetaClass().getClazz(), bean);
            }
        }
    }

    public static SeeTheEyeBuilder prepare() {
        return Guice.createInjector(new SeeTheEyeGuiceModule()).getInstance(SeeTheEyeBuilder.class);
    }

    public <T> T get(Class<T> beanType) {
        LOG.debug("get(beanType={})", beanType.getName());
        Optional<Bean> bean = findBean(beanType);
        if (!bean.isPresent()) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }
        return getRecursive(bean.get());
    }

    private <T> T getRecursive(Bean bean) {
        LOG.debug("getRecursive(bean={})", bean);

        if (bean.getUserDefinedInstance().isPresent()) {
            LOG.trace("Returning user defined instance: {}", bean.getUserDefinedInstance().get());
            return (T) bean.getUserDefinedInstance().get();
        }
        if (bean.isSingletonAnnotationPresent()) {
            return lookupSingleton(bean);
        }

        return bean.getScope().actOn(new Scope.ScopeCallback<T>() {
            @Override public T onPrototype() {
                List<Class<?>> dependencies = bean.getDependencies();

                List<Object> arguments = new ArrayList<>(dependencies.size());
                for (Class<?> dependency : dependencies) {
                    LOG.trace("Recursively getting dependency bean of type: {}", dependency.getName());
                    Optional<Bean> subBean = findBean(dependency);
                    if (!subBean.isPresent()) {
                        throw new SeeTheEyeException.DependencyResolveException(bean.getMetaClass().getClazz(), dependency);
                    }
                    Object foundDependency = getRecursive(subBean.get());
                    arguments.add(foundDependency);
                }

                Object instance = bean.newInstance(arguments);
                LOG.trace("Returning prototype scoped new instance: {}", instance);
                return (T) instance;
            }
            @Override public T onSingelton() {
                return lookupSingleton(bean);
            }
        });
    }

    private <T> T lookupSingleton(Bean bean) {
        Object cachedInstance = singletonsByBean.get(bean);
        if (cachedInstance != null) {
            LOG.trace("Returning cached singleton instance: {}", cachedInstance);
            return (T) cachedInstance;
        }
        T instance = bean.newInstance(Collections.emptyList()); // FIXME implement injection for singletons
        singletonsByBean.put(bean, instance);
        LOG.trace("Returning initially created singleton instance: {}", cachedInstance);
        return instance;
    }

    private Optional<Bean> findBean(Class<?> beanType) {
        Bean byConcreteType = beansByType.get(beanType);
        if (byConcreteType != null) {
            return Optional.of(byConcreteType);
        }
        if (beansByInterface.containsKey(beanType)) {
            return Optional.of(beansByInterface.get(beanType));
        }
        return Optional.empty();
    }

}
