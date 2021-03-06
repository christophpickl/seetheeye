package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.ReflectionException;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.Declaration;
import com.github.christophpickl.seetheeye.api.configuration.ProviderDeclaration;

import javax.inject.Provider;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class PreValidator {

    // TODO use message templates instead

    private final Collection<ConfigurationDeclaration> declarations;
    private final Collection<String> errorMessages = new LinkedList<>();
    private final Set<MetaClass> registeredTypes = new HashSet<>();

    public PreValidator(Collection<ConfigurationDeclaration> declarations) {
        this.declarations = declarations;
    }

    Collection<String> detect() {
        for (ConfigurationDeclaration declaration : declarations) {
            declaration.getBeans().forEach(this::isBeanTypeAConcreteAccessibleClass);
            declaration.getBeans().forEach(this::isNotRegisteredMultipleTimes);
            declaration.getBeans().forEach(this::isReallyImplementingRegisteredTypes);

            declaration.getInstances().forEach(this::isReallyImplementingRegisteredTypes);
            declaration.getInstances().forEach(this::isNotRegisteredMultipleTimes);

            declaration.getProviders().forEach(this::isValidProvider);

            // TODO MINOR would be nice to to have the config class name where the invalid stuff was configured
//            String configName = declaration.getOriginalUserConfiguration().getClass().getName();
//            errorMessages.addAll(.stream().map(beanError -> beanError + " (Defining configuration was: " + configName + ")").collect(Collectors.toList()));
        }
        return errorMessages;
    }

    private void isValidProvider(ProviderDeclaration declaration) {
        MetaClass providerType = declaration.getInstallType();
        if (!Provider.class.isAssignableFrom(providerType.getEnclosedClass())) {
            errorMessages.add("Configured provider does not implement the " + Provider.class.getName() + " interface!");
            return;
        }
        MetaClass provideeType;
        try {
            // just try it and if it fails report with cumulative errors
            provideeType = providerType.getSingleTypeParamaterOfSingleInterface();
        } catch (ReflectionException.InvalidTypeException e) {
            errorMessages.add(e.getMessage());
            return;
        }
        if (registeredTypes.contains(provideeType)) {
            // TODO copy'n'paste error message
            errorMessages.add("Duplicate bean definition for '" + provideeType.getName() + "'!");
        }
        registeredTypes.add(provideeType);
    }

    private void isBeanTypeAConcreteAccessibleClass(BeanDeclaration declaration) {
        MetaClass beanType = declaration.getInstallType();
        String errorMessagePrefix = "Invalid bean type " + beanType.getName() + "! Explanation: ";
        if (beanType.isInterface()) {
            errorMessages.add(errorMessagePrefix + "Interfaces are not allowed!");
        } else if (beanType.isAbstract()) {
            errorMessages.add(errorMessagePrefix + "Abstract classes are not allowed!");
        } else if (beanType.isInnerClass()) {
            errorMessages.add(errorMessagePrefix + "Inner classes are not allowed!");
        }
    }

    private void isNotRegisteredMultipleTimes(Declaration declaration) {
        MetaClass beanType = declaration.getInstallType();
        Collection<MetaClass> registrationTypes = declaration.getRegistrationTypes();
        if (!registrationTypes.isEmpty()) {
            for (MetaClass registrationType : registrationTypes) {
                if (!registrationType.isInterface()) {
                    errorMessages.add("Given a non-interface for the 'as-type' for bean: " + beanType.getName());
                    continue;
                }
                if (registeredTypes.contains(registrationType)) {
                    errorMessages.add("Duplicate bean definition '" + beanType.getName() + "' " +
                            "for interface '" + registrationType.getName() + "'!");
                }
                registeredTypes.add(registrationType);
            }

        } else {
            // only registered as bean by itself, without any given as-interface.
            if (registeredTypes.contains(beanType)) {
                errorMessages.add("Duplicate bean definition for '" + beanType.getName() + "'!");
            }
            registeredTypes.add(beanType);
        }
    }

    private void isReallyImplementingRegisteredTypes(Declaration declaration) {
        Collection<MetaClass> registrationTypes = declaration.getRegistrationTypes();
        if (registrationTypes.isEmpty()) {
            return;
        }
        MetaClass beanType = declaration.getInstallType();
        for (MetaClass registrationType : registrationTypes) {
            if (!registrationType.isInterface()) {
                errorMessages.add("Beans can only be configured as interfaces! Given type was: " + registrationType.getName());
                continue;
            }
            if (!beanType.isImplementing(registrationType)) {
                errorMessages.add("Given bean '" + beanType.getName() + "' does not implement interface '" + registrationType.getName() + "'!");
            }
        }

    }

}
