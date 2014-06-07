package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.InstanceDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.Scope;
import com.github.christophpickl.seetheeye.impl2.Context;
import com.github.christophpickl.seetheeye.impl2.configuration.*;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ContextFactoryImpl implements ContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ContextFactoryImpl.class);

    private final BeanAnalyzer analyzer;
    private final ConfigurationValidator validator;

    @Inject
    public ContextFactoryImpl(BeanAnalyzer analyzer, ConfigurationValidator validator) {
        this.analyzer = analyzer;
        this.validator = validator;
    }


    public Context create(Collection<ConfigurationDeclaration> configurations) {
        LOG.debug("create(configurations)");
        validator.validatePre(configurations);

        Collection<Definition<?>> definitions = new LinkedList<>();
        for (ConfigurationDeclaration configuration : configurations) {
            definitions.addAll(createBeans(configuration.getBeans()));
            definitions.addAll(createInstances(configuration.getInstances()));
        }
        DefinitionRepository repo = new DefinitionRepository(definitions);
        validator.validatePost(repo);
        return new Context(repo);
    }

    private Collection<InstanceDefinition<?>> createInstances(Collection<InstanceDeclaration> instances) {
        return instances.stream().map(this::createInstanceDefinition).collect(Collectors.toList());
    }

    private InstanceDefinition<?> createInstanceDefinition(InstanceDeclaration declaration) {
        return new InstanceDefinition<>(declaration.getInstance(), declaration.getRegistrationTypes());
    }

    private Collection<BeanDefinition<?>> createBeans(Collection<BeanDeclaration> beans) {
        return beans.stream().map(this::createBeanDefinition).collect(Collectors.toList());
    }

    private BeanDefinition<?> createBeanDefinition(BeanDeclaration declaration) {
        Constructor constructor = analyzer.findProperConstructor(declaration.getInstallType());
        Collection<MetaClass> dependencies = analyzer.findDependencies(constructor);

        Collection<MetaClass> registrationTypes = declaration.getRegistrationTypes();
        if (declaration.getScope() == Scope.SINGLETON) {
            // TODO seperate interface type in second arg ;)
            return new SingletonBeanDefinition(declaration.getInstallType(), registrationTypes, constructor, dependencies);
        }
        return new BeanDefinition(declaration.getInstallType(), registrationTypes, constructor, dependencies);
    }

}
