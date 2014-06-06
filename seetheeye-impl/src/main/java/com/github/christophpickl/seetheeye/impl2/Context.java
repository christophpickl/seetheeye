package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<Class<?>, BeanDefinitionX> beansByType = new HashMap<>();

    public Context(Collection<BeanDefinitionX> beans) {
        for (BeanDefinitionX bean : beans) {
            beansByType.put(bean.getRegistrationType().getInnerType(), bean);
        }
    }

    public <T> T get(Class<T> beanType) {
        if (!beansByType.containsKey(beanType)) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }
        BeanDefinitionX bean = beansByType.get(beanType);
        return (T) bean.instance();
    }
}
