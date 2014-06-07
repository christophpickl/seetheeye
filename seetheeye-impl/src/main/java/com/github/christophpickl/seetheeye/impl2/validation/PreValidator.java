package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

class PreValidator {

    private final Collection<ConfigurationDefinition> definitions;

    public PreValidator(Collection<ConfigurationDefinition> definitions) {
        this.definitions = definitions;
    }

    Collection<String> detect() {
        Collection<String> errorMessages = new LinkedList<>();

        for (ConfigurationDefinition definition : definitions) {
            String configName = definition.getOriginalUserConfiguration().getClass().getName();
            errorMessages.addAll(validateBeanDefinitions(definition.getBeans())
                    .stream().map(beanError -> beanError + " (Defining configuration was: " + configName + ")").collect(Collectors.toList()));
        }
        return errorMessages;
    }

    private Collection<String> validateBeanDefinitions(Collection<BeanDefinition> beans) {
        Collection<String> errorMessages = new LinkedList<>();
        Set<MetaClass> registeredBeanTypes = new HashSet<>();
        for (BeanDefinition bean : beans) {
            MetaClass beanType = bean.getBeanType();
            if (registeredBeanTypes.contains(beanType)) {
                errorMessages.add("Duplicate bean definition for '" + beanType.getName() + "'!");
            }
            registeredBeanTypes.add(beanType);
            String errorMessagePrefix = "Invalid bean type " + beanType.getName() + "! Explanation: ";
            if (beanType.isInterface()) {
                errorMessages.add(errorMessagePrefix + "Interfaces are not allowed!");
            } else if (beanType.isAbstract()) {
                errorMessages.add(errorMessagePrefix + "Abstract classes are not allowed!");
            } else if (beanType.isInnerClass()) {
                errorMessages.add(errorMessagePrefix + "Inner classes are not allowed!");
            }
        }
        return errorMessages;
    }
}
