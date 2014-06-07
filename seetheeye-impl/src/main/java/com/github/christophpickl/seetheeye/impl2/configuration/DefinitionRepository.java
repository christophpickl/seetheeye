package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefinitionRepository {

    private final Map<Class<?>, Definition<?>> definitionsByRegistrationType = new HashMap<>();

    private final Collection<BeanDefinition<?>> beans;

    public DefinitionRepository(Collection<BeanDefinition<?>> beans) {
        this.beans = beans;
        for (BeanDefinition<?> bean : beans) {
            for (MetaClass registrationType : bean.getRegistrationTypesOrInstallType()) {
                definitionsByRegistrationType.put(registrationType.getEnclosedClass(), bean);
            }
        }
    }

    public Collection<BeanDefinition<?>> getBeans() {
        return beans;
    }

    public <T> boolean isRegistered(Class<T> beanType) {
        return definitionsByRegistrationType.containsKey(beanType);
    }

    public <T> Definition lookupRegistered(Class<T> beanType) {
        if (!isRegistered(beanType)) throw new IllegalStateException("Not registered bean: " + beanType.getName());
        return definitionsByRegistrationType.get(beanType);
    }

}
