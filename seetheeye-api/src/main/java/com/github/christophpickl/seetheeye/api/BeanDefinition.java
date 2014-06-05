package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;

public class BeanDefinition {

    private final Class<?> beanType;

    public BeanDefinition(Class<?> beanType) {
        this.beanType = beanType;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("beanType", beanType)
                .toString();
    }

}
