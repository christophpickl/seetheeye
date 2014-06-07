package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.configuration.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.ReflectionUtil;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.enterprise.event.Event;
import javax.inject.Provider;
import java.util.*;

/**
 * Main entry point.
 */
@Deprecated
public class SeeTheEye implements SeeTheEyeApi, ObserverRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEye.class);

    private final Collection<Bean> beans;
    private final Collection<Class<? extends Provider<?>>> providers;

    private final Map<Class<?>, Bean> beansByType = new HashMap<>();
    private final Map<Class<?>, Bean> beansByInterface = new HashMap<>();
    private final Map<Bean, Object> singletonsByBean = new HashMap<>();
    // TODO refactor! introduce own providerRepository datastructure. contains singleton instances of each provider (doesnt need to be recreated each time)
    private final Map<Class<?>, Class<? extends Provider<?>>> providersByBeanType = new HashMap<>();
    private final Map<Class<?>, Collection<EventObserverInstance>> observerInstancesByEventType = new HashMap<>();

    static class EventObserverInstance {

        private final EventObserver observer;
        private final Object instance;

        public EventObserverInstance(EventObserver observer, Object instance) {
            this.observer = observer;
            this.instance = instance;
        }

        public <T> void dispatch(T value) {
            observer.invokeMethod(instance, value);
        }
    }

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
        // TODO ParameterizedTypeImpl is deprecated
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
            return (T) ReflectionUtil.instantiate(providerType.getDeclaredConstructors()[0]);
        }
        if (providersByBeanType.containsKey(beanType)) {
            Class<? extends Provider<T>> providerType = (Class<? extends Provider<T>>) providersByBeanType.get(beanType);
            // TODO figure out proper constructor for provider
            LOG.trace("Returning provider instance by: {}", providerType.getName());
            Provider<T> provider = (Provider<T>) ReflectionUtil.instantiate(providerType.getDeclaredConstructors()[0]);
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
                return lookupPrototype(bean);
            }
            @Override public T onSingelton() {
                return lookupSingleton(bean);
            }
        });
    }

    private List<Object> createArguments(Bean bean) {
        List<Class<?>> dependencies = bean.getDependencies();
        List<Object> arguments = new ArrayList<>(dependencies.size());
        for (Class<?> dependency : dependencies) {
            LOG.trace("Recursively getting dependency bean of type: {}", dependency.getName());
            if (Event.class == dependency) {
                // FIXME hardcoded String observer
                Class<?> eventType = String.class;
                LOG.trace("Adding event dispatcher for dependency of type {} for bean: {}",
                        dependency.getName(), bean);
                arguments.add(new EventDispatcher(SeeTheEye.this, eventType)); // TODO no generics?!
                // FIXME when to let loose of this (weak) reference??
                continue;
            }
            Optional<Bean> subBean = findBean(dependency);
            if (!subBean.isPresent()) {
                throw new SeeTheEyeException.DependencyResolveException(bean.getMetaClass().getClazz(), dependency);
            }
            Object foundDependency = getRecursive(subBean.get());
            arguments.add(foundDependency);
        }
        return arguments;
    }

    private <T> T lookupPrototype(Bean bean) {
        T instance = instantiateBean(bean);
        LOG.trace("Returning prototype scoped new instance: {}", instance);
        return instance;
    }

    private <T> T instantiateBean(Bean bean) {
        List<Object> arguments = createArguments(bean);
        Object instance = bean.newInstance(arguments);
        registerObserverForPrototype(bean, instance);
        return (T) instance;
    }

    private void registerObserverForPrototype(Bean bean, Object instance) {
        for (EventObserver observer : bean.getObservers()) {
            if  (!observerInstancesByEventType.containsKey(observer.getEventType())) {
                observerInstancesByEventType.put(observer.getEventType(), new LinkedHashSet<>());
            }
            LOG.trace("Adding instance observer {} for bean: {}", observer, bean);
            observerInstancesByEventType.get(observer.getEventType())
                    .add(new EventObserverInstance(observer, instance));
        }
    }

    private <T> T lookupSingleton(Bean bean) {
        Object cachedInstance = singletonsByBean.get(bean);
        if (cachedInstance != null) {
            LOG.trace("Returning cached singleton instance: {}", cachedInstance);
            return (T) cachedInstance;
        }
        T instance = instantiateBean(bean);
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

    // ObserverRepository
    @Override public <T> void dispatch(Class<T> type, T value) { // Event<T> event ... not used
        // TODO check if any is registered at all!
        Collection<EventObserverInstance> observers = observerInstancesByEventType.get(type);
        for (EventObserverInstance observer : observers) {
            observer.dispatch(value);
        }
    }
}
