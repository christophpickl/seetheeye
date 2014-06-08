package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.ReflectionException;
import com.github.christophpickl.seetheeye.api.ReflectionUtil;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;

import javax.enterprise.event.Event;
import javax.inject.Provider;
import java.util.*;

/**
 * Main entry point.
 */
@Deprecated
public class SeeTheEye implements SeeTheEyeApi, ObserverRepository {

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

    }

    public static SeeTheEyeBuilder prepare() {
        return new SeeTheEyeBuilder();
    }

    public <T> T get(Class<T> beanType) {
        if (providersByBeanType.containsKey(beanType)) {
            Class<? extends Provider<T>> providerType = (Class<? extends Provider<T>>) providersByBeanType.get(beanType);
            // TODO figure out proper constructor for provider
            Provider<T> provider = (Provider<T>) ReflectionUtil.instantiate(providerType.getDeclaredConstructors()[0]);
            return provider.get();
        }

        return null;
    }

    private List<Object> createArguments(Bean bean) {
        List<Class<?>> dependencies = bean.getDependencies();
        List<Object> arguments = new ArrayList<>(dependencies.size());
        for (Class<?> dependency : dependencies) {
            if (Event.class == dependency) {
                // FIXME hardcoded String observer
                Class<?> eventType = String.class;
                arguments.add(new EventDispatcher(SeeTheEye.this, eventType)); // TODO no generics?!
                // FIXME when to let loose of this (weak) reference??
                continue;
            }
            Optional<Bean> subBean = null;
            if (!subBean.isPresent()) {
                throw new SeeTheEyeException.DependencyResolveException(bean.getMetaClass().getClazz(), dependency);
            }
            Object foundDependency = null;
            arguments.add(foundDependency);
        }
        return arguments;
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
            observerInstancesByEventType.get(observer.getEventType())
                    .add(new EventObserverInstance(observer, instance));
        }
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
