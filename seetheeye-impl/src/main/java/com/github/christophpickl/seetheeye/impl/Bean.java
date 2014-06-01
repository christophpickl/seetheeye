package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;

import java.util.Optional;

class Bean implements BeanConfigurationPostProcessor {

    private final MetaClass beanType;

    private Scope scope = Scope.PROTOTYPE;

    private Optional<Class<?>> beanInterface = Optional.empty();

    private Optional<Object> userDefinedInstance = Optional.empty();

    public Bean(Class<?> beanType) {
        this.beanType = new MetaClass(Preconditions.checkNotNull(beanType));
    }

    @Override public BeanConfigurationPostProcessor inScope(Scope scope) {
        this.scope = Preconditions.checkNotNull(scope);
        return this;
    }

    @Override public BeanConfigurationPostProcessor as(Class<?> beanInterface) {
        Preconditions.checkNotNull(beanInterface);
        if (!beanInterface.isInterface()) {
            throw new SeeTheEyeException.ConfigInvalidException("Must be an interface type: " + beanInterface.getName());
        }
        if (!beanType.isImplementing(beanInterface)) {
            throw new SeeTheEyeException.ConfigInvalidException("Bean '" + beanType.getName() + "' does not implement interface: " + beanInterface.getName());
        }

        this.beanInterface = Optional.of(beanInterface);
        return this;
    }

    public MetaClass getBeanType() {
        return beanType;
    }

    public Optional<Class<?>> getBeanInterface() {
        return beanInterface;
    }


    public <T> T newInstance() {
        return (T) beanType.newInstance();
    }

    public Scope getScope() {
        return scope;
    }

    public Optional<Object> getUserDefinedInstance() {
        return userDefinedInstance;
    }

    public void setUserDefinedInstance(Object userDefinedInstance) {
        this.userDefinedInstance = Optional.of(Preconditions.checkNotNull(userDefinedInstance));
    }


}
