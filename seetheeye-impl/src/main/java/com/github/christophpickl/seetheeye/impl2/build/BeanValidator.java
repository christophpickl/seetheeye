package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class BeanValidator {

    private static final Logger LOG = LoggerFactory.getLogger(BeanValidator.class);

    public void validate(Collection<ConfigurationDefinition> definitions) {
        LOG.debug("validate(definitions)");
        Collection<String> errorMessages = new LinkedList<>();

        for (ConfigurationDefinition definition : definitions) {
            String configName = definition.getOriginalUserConfiguration().getClass().getName();
            errorMessages.addAll(validateBeanDefinitions(definition.getBeans())
                .stream().map(beanError -> beanError + " (Defining configuration was: " + configName + ")").collect(Collectors.toList()));
        }


        if (!errorMessages.isEmpty()) {
            String prefix = "\n\t- ";
            throw new SeeTheEyeException.ConfigInvalidException(
                "Following errors occured:" + prefix + Joiner.on(prefix).join(errorMessages));
        }
    }

    private Collection<String> validateBeanDefinitions(Collection<BeanDefinition> beans) {
        Collection<String> errorMessages = new LinkedList<>();
        for (BeanDefinition bean : beans) {
            MetaClass<?> beanType = bean.getBeanType();
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
