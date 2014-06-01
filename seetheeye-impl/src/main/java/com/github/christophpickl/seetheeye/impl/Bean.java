package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

class Bean implements BeanConfigurationPostProcessor {

    private final MetaClass metaClass;

    private Scope scope = Scope.PROTOTYPE;

    private Optional<Class<?>> beanInterface = Optional.empty();

    private Optional<Object> userDefinedInstance = Optional.empty();

    private final boolean singletonAnnotationPresent;

    private final List<Class<?>> dependencies;

    public Bean(Class<?> beanType) {
        this.metaClass = new MetaClass(Preconditions.checkNotNull(beanType));
        this.singletonAnnotationPresent = metaClass.hasAnnotation(Singleton.class);
        this.dependencies = metaClass.getConstructorParameters();
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
        if (!metaClass.isImplementing(beanInterface)) {
            throw new SeeTheEyeException.ConfigInvalidException("Bean '" + metaClass.getName() + "' does not implement interface: " + beanInterface.getName());
        }

        this.beanInterface = Optional.of(beanInterface);
        return this;
    }

    public MetaClass getMetaClass() {
        return metaClass;
    }

    public Optional<Class<?>> getBeanInterface() {
        return beanInterface;
    }


    public <T> T newInstance(List<Object> arguments) {
        return (T) metaClass.newInstance(arguments.toArray());
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


    public boolean isSingletonAnnotationPresent() {
        return singletonAnnotationPresent;
    }

    public List<Class<?>> getDependencies() {
        return dependencies;
    }
}
