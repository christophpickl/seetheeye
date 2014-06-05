package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

public class BeanValidator {

    private static final Logger LOG = LoggerFactory.getLogger(BeanValidator.class);

    public void validate(Collection<ConfigurationDefinition> definitions) {
        Collection<String> errorMessages = new LinkedList<>();

        for (ConfigurationDefinition definition : definitions) {
            String configName = definition.getOriginalUserConfiguration().getClass().getName();
            for (BeanDefinition bean : definition.getBeans()) {
                Class<?> beanType = bean.getBeanType();
                String errorMessagePrefix = "Invalid bean type " + beanType.getName() +
                        " in configuration " + configName + "! Explanation: ";

                int modifiers = beanType.getModifiers();
                boolean isAbstract = Modifier.isAbstract(modifiers);
                boolean isStatic = Modifier.isStatic(modifiers);
                boolean isInnerClass = beanType.getDeclaringClass() != null && !isStatic;
                LOG.trace("Validating type {}: abstract={}, static={}, isInnerClass={}, declaringClass={}",
                        beanType.getSimpleName(), isAbstract, isStatic, isInnerClass,
                        beanType.getDeclaringClass() == null ? "N/A" : beanType.getDeclaringClass().getName());

                if (beanType.isInterface()) {
                    errorMessages.add(errorMessagePrefix + "Interfaces are not allowed!");
                } else if (isAbstract) {
                    errorMessages.add(errorMessagePrefix + "Abstract classes are not allowed!");
                } else if (isInnerClass) {
                    errorMessages.add(errorMessagePrefix + "Inner classes are not allowed!");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            String prefix = "\n\t- ";
            throw new SeeTheEyeException.ConfigInvalidException(
                "Following errors occured:" + prefix + Joiner.on(prefix).join(errorMessages));
        }
    }

}
