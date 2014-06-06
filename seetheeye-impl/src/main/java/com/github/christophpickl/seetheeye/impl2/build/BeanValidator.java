package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.BeanDefinitionX;
import com.github.christophpickl.seetheeye.impl2.DefinitionRepository;
import com.github.christophpickl.seetheeye.impl2.DefinitionX;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanValidator {

    private static final Logger LOG = LoggerFactory.getLogger(BeanValidator.class);

    public void validatePre(Collection<ConfigurationDefinition> definitions) {
        LOG.debug("validatePre(definitions)");
        Collection<String> errorMessages = new LinkedList<>();

        for (ConfigurationDefinition definition : definitions) {
            String configName = definition.getOriginalUserConfiguration().getClass().getName();
            errorMessages.addAll(validateBeanDefinitions(definition.getBeans())
                .stream().map(beanError -> beanError + " (Defining configuration was: " + configName + ")").collect(Collectors.toList()));
        }


        throwExceptionIfErrorExists(errorMessages);
    }


    public void validatePost(DefinitionRepository repo) {
        LOG.debug("validatePost(repo)");
        Collection<String> errorMessages = new LinkedList<>();
        errorMessages.addAll(new DependencyCycleDetector(repo).detect());
        throwExceptionIfErrorExists(errorMessages);
    }

    private static class DependencyCycleDetector {
        private final DefinitionRepository repo;
        private final Collection<String> errors = new LinkedList<>();
        private final Set<DefinitionX<?>> markedBeans = new LinkedHashSet<>();

        private DependencyCycleDetector(DefinitionRepository repo) {
            this.repo = repo;
        }

        public Collection<String> detect() {
            f(repo);
            return errors;
        }

        private void f(DefinitionRepository repo) {
            for (BeanDefinitionX<?> bean : repo.getBeans()) {
                x(bean);
            }
        }

        private void x(DefinitionX bean) {
            if (markedBeans.contains(bean)) {
                errors.add("Found cyclic dependency for bean: " + bean.getRegistrationType().getName());
                return;
            }
            markedBeans.add(bean);
            Collection<MetaClass> dependencies = bean.getDependencies();
            for (MetaClass dependency : dependencies) {
                if (!repo.isRegistered(dependency.getEnclosedClass())) {
                    errors.add("Unresolved dependency of type " + dependency.getName() +
                            " for bean: " + bean.getInstallType());
                    continue;
                }
                DefinitionX dependencyDefinition = repo.lookupRegistered(dependency.getEnclosedClass());
                x(dependencyDefinition);
            }
            markedBeans.remove(bean);
        }
    }

    private Collection<String> validateBeanDefinitions(Collection<BeanDefinition> beans) {
        Collection<String> errorMessages = new LinkedList<>();
        for (BeanDefinition bean : beans) {
            MetaClass beanType = bean.getBeanType();
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

    private static void throwExceptionIfErrorExists(Collection<String> errorMessages) {
        if (!errorMessages.isEmpty()) {
            String prefix = "\n\t- ";
            throw new SeeTheEyeException.ConfigInvalidException(
                    "Following errors occured:" + prefix + Joiner.on(prefix).join(errorMessages));
        }
    }
}
