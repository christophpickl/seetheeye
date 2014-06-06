package com.github.christophpickl.seetheeye.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Deprecated
public class EventObserver {

//    private final Bean bean;

    private final Class<?> eventType;

    private final Method method;

    public EventObserver(Class<?> eventType, Method method) {
        this.eventType = eventType;
        this.method = method;
        this.method.setAccessible(true); // TODO minor reflection hack ;)
    }

//    public Bean getBean() {
//        return bean;
//    }

    public Class<?> getEventType() {
        return eventType;
    }

    public <T> void invokeMethod(Object instance, T value) {
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException  e) {
            throw new RuntimeException("Could not invoke method: " + method);
        }
    }
}
