package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Main entry point.
 */
public class SeeTheEye implements SeeTheEyeApi {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEye.class);

    private Collection<Bean> beans;
    private Collection<Class<? extends Provider<?>>> providers;

    private final Map<Class<?>, Bean> beansByType = new HashMap<>();
    private final Map<Class<?>, Bean> beansByInterface = new HashMap<>();
    private final Map<Bean, Object> singletonsByBean = new HashMap<>();
    // TODO refactor! introduce own providerRepository datastructure. contains singleton instances of each provider (doesnt need to be recreated each time)
    private final Map<Class<?>, Class<? extends Provider<?>>> providersByBeanType = new HashMap<>();

    SeeTheEye(Collection<Bean> beans, Collection<Class<? extends Provider<?>>> providers) {
        this.beans = Preconditions.checkNotNull(beans);
        this.providers = Preconditions.checkNotNull(providers);
        LOG.trace("new (beans={}, providers={})", Arrays.toString(beans.toArray()), Arrays.toString(providers.toArray()));
        for (Bean bean : beans) {
            if (bean.getBeanInterface().isPresent()) {
                LOG.trace("Registering bean of type '{}' to interface type '{}'.",
                    bean.getMetaClass().getName(), bean.getBeanInterface().get().getName());
                beansByInterface.put(bean.getBeanInterface().get(), bean);
            } else {
                beansByType.put(bean.getMetaClass().getClazz(), bean);
            }
        }

        for (Class<? extends Provider<?>> provider : providers) {
            Class<?> providingBeanType = extractProviderTypeParameter(provider);
            providersByBeanType.put(providingBeanType, provider);
        }
    }

    // also needs to be refactored
    static Class<?> extractProviderTypeParameter(Class<? extends Provider<?>> provider) {
        ParameterizedTypeImpl providerInterfaceGeneric = (ParameterizedTypeImpl) provider.getGenericInterfaces()[0];
        return (Class<?>) providerInterfaceGeneric.getActualTypeArguments()[0];
    }

    public static SeeTheEyeBuilder prepare() {
//        TODO enable guice: return Guice.createInjector(new SeeTheEyeGuiceModule()).getInstance(SeeTheEyeBuilder.class);
        return new SeeTheEyeBuilder();
    }

    public <T> T get(Class<T> beanType) {
        LOG.debug("get(beanType={})", beanType.getName());
        if (Provider.class.isAssignableFrom(beanType)) {
            Class<? extends Provider<Object>> providerType = (Class<? extends Provider<Object>>)beanType;
            // TODO figure out proper constructor for provider
            LOG.trace("Returning provider: {}", providerType.getName());
            return (T) Reflections.instantiate(providerType.getDeclaredConstructors()[0]);
        }
        if (providersByBeanType.containsKey(beanType)) {
            Class<? extends Provider<T>> providerType = (Class<? extends Provider<T>>) providersByBeanType.get(beanType);
            // TODO figure out proper constructor for provider
            LOG.trace("Returning provider instance by: {}", providerType.getName());
            Provider<T> provider = (Provider<T>) Reflections.instantiate(providerType.getDeclaredConstructors()[0]);
            return provider.get();
        }

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
                    if (Event.class == dependency) {
                        arguments.add(new MyEvent()); // no generics ;)
                        continue;
                    }
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

    static class MyEvent<T> implements Event<T> {

        @Override
        public void fire(T t) {
            System.out.println("fireee: " + t);
            // report back to see-the-eye, which holds a list of beans observing that event
        }

        @Override
        public Event<T> select(Annotation... annotations) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public <U extends T> Event<U> select(Class<U> uClass, Annotation... annotations) {
            throw new UnsupportedOperationException("Not implemented!");
        }

        @Override
        public <U extends T> Event<U> select(TypeLiteral<U> uTypeLiteral, Annotation... annotations) {
            throw new UnsupportedOperationException("Not implemented!");
        }
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
