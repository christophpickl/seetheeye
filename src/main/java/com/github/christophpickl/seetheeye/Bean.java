package com.github.christophpickl.seetheeye;

import com.google.common.base.Preconditions;

class Bean implements BeanConfigurationPostProcessor {

    private final Class<?> beanType;

    private Scope scope = Scope.PROTOTYPE;

    public Bean(Class<?> beanType) {
        this.beanType = beanType;
    }


    @Override
    public BeanConfigurationPostProcessor inScope(Scope scope) {
        this.scope = Preconditions.checkNotNull(scope);
        return this;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public <T> T newInstance() {
        try {
            return (T) beanType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SeeTheEyeException.BeanInstantiationException(beanType, e);
        }
    }

    public Scope getScope() {
        return scope;
    }
}
