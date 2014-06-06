package com.github.christophpickl.seetheeye.impl2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefinitionRepository {

    private final Map<Class<?>, DefinitionX<?>> definitionsByRegistrationType = new HashMap<>();

    private final Collection<BeanDefinitionX<?>> beans;

    public DefinitionRepository(Collection<BeanDefinitionX<?>> beans) {
        this.beans = beans;
        for (BeanDefinitionX bean : beans) {
            definitionsByRegistrationType.put(bean.getRegistrationType().getEnclosedClass(), bean);
        }
    }

    public Collection<BeanDefinitionX<?>> getBeans() {
        return beans;
    }

    public <T> boolean isRegistered(Class<T> beanType) {
        return definitionsByRegistrationType.containsKey(beanType);
    }

    public <T> DefinitionX lookupRegistered(Class<T> beanType) {
        if (!isRegistered(beanType)) throw new IllegalStateException("Not registered bean: " + beanType.getName());
        return definitionsByRegistrationType.get(beanType);
    }

}
