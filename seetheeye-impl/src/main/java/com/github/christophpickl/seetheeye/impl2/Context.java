package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<Class<?>, DefinitionX> definitionsByRegistrationType = new HashMap<>();

    public Context(Collection<BeanDefinitionX> beans) {
        for (BeanDefinitionX bean : beans) {
            definitionsByRegistrationType.put(bean.getRegistrationType().getEnclosedClass(), bean);
        }
    }

    public <T> T get(Class<T> beanType) {
        if (!definitionsByRegistrationType.containsKey(beanType)) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }
        DefinitionX bean = definitionsByRegistrationType.get(beanType);
        return (T) bean.instance();
    }
}
