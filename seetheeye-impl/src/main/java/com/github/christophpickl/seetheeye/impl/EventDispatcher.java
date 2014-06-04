package com.github.christophpickl.seetheeye.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;

class EventDispatcher<T> implements Event<T> {

    private static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);

    private final ObserverRepository repo;

    private final Class<T> eventType;

    EventDispatcher(ObserverRepository repo, Class<T> eventType) {
        this.repo = repo;
        this.eventType = eventType;
    }

    @Override
    public void fire(T value) {
        LOG.debug("fire(value={})", value);
        repo.dispatch(eventType, value);
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
