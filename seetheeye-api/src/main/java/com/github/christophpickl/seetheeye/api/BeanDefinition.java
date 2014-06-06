package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.inject.Singleton;

public class BeanDefinition {

    private static final Scope DEFAULT_SCOPE = Scope.PROTOTYPE;

    private final MetaClass<?> beanType;

    private Scope scope;

    public BeanDefinition(MetaClass<?> beanType) {
        this.beanType = beanType;
        this.scope = beanType.isAnnotationPresent(Singleton.class) ? Scope.SINGLETON : DEFAULT_SCOPE;
    }

    public MetaClass<?> getBeanType() {
        return beanType;
    }

    public void setScope(Scope scope) {
        this.scope = Preconditions.checkNotNull(scope);
    }

    // TODO should be internal only
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("beanType", beanType)
                .toString();
    }
}
