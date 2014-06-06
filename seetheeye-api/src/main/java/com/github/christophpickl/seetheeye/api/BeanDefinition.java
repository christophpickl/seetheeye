package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;

public class BeanDefinition {

    private final MetaClass<?> beanType;

    public BeanDefinition(MetaClass<?> beanType) {
        this.beanType = beanType;
    }

    public MetaClass<?> getBeanType() {
        return beanType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("beanType", beanType)
                .toString();
    }

}
