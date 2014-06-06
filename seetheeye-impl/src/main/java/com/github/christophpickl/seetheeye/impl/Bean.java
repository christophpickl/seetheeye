package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.BeanConfigurationPostProcessor;
import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.ReflectionUtil;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Deprecated
class Bean implements BeanConfigurationPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(Bean.class);

    private final Class<?> beanType;

    private final MetaClass metaClass;

    private Scope scope = Scope.PROTOTYPE;

    private Optional<Class<?>> beanInterface = Optional.empty();

    private Optional<Object> userDefinedInstance = Optional.empty();

    private final boolean singletonAnnotationPresent;

    private final List<Class<?>> dependencies;

    private final Collection<EventObserver> observers = new LinkedHashSet<>();

    public Bean(Class<?> beanType) {
        this.beanType = beanType;
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
        return (T) ReflectionUtil.instantiate(metaClass.getConstructor(), arguments.toArray());
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

    public Class<?> getBeanType() {
        return beanType;
    }

    public void addObserver(EventObserver observer) {
        LOG.debug("Adding observer {} for this bean {}", observer, this);
        observers.add(observer);
    }

    public Collection<EventObserver> getObservers () {
        return observers;
    }
}
