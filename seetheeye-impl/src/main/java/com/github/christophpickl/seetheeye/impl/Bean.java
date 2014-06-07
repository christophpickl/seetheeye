package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.configuration.Scope;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Deprecated
class Bean implements BeanConfigurationPostProcessor {



    public void addObserver(EventObserver observer) {
//        LOG.debug("Adding observer {} for this bean {}", observer, this);
//        observers.add(observer);
    }

    public Collection<EventObserver> getObservers () {
        return null;
    }







    public Bean(Class<?> beanType) {
    }

    @Override public BeanConfigurationPostProcessor inScope(Scope scope) {
        return null;
    }

    @Override public BeanConfigurationPostProcessor as(Class<?> beanInterface) {
        return null;
    }

    public MetaClass getMetaClass() {
        return null;
    }

    public Optional<Class<?>> getBeanInterface() {
        return null;
    }


    public <T> T newInstance(List<Object> arguments) {
        return null;
    }

    public Scope getScope() {
        return null;
    }

    public Optional<Object> getUserDefinedInstance() {
        return null;
    }

    public void setUserDefinedInstance(Object userDefinedInstance) {
    }


    public boolean isSingletonAnnotationPresent() {
        return false;
    }

    public List<Class<?>> getDependencies() {
        return null;
    }

    public Class<?> getBeanType() {
        return null;
    }

}
