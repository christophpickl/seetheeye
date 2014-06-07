package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.Scope;
import com.github.christophpickl.seetheeye.impl2.*;
import com.github.christophpickl.seetheeye.impl2.configuration.BeanDefinition;
import com.github.christophpickl.seetheeye.impl2.configuration.DefinitionRepository;
import com.github.christophpickl.seetheeye.impl2.configuration.SingletonBeanDefinition;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;

public class ContextFactoryImpl implements ContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ContextFactoryImpl.class);

    private final BeanAnalyzer analyzer;
    private final ConfigurationValidator validator;

    @Inject
    public ContextFactoryImpl(BeanAnalyzer analyzer, ConfigurationValidator validator) {
        this.analyzer = analyzer;
        this.validator = validator;
    }


    public Context create(Collection<ConfigurationDeclaration> declarations) {
        LOG.debug("create(declarations)");
        validator.validatePre(declarations);

        Collection<BeanDefinition<?>> beans = createBeans(declarations);
        DefinitionRepository repo = new DefinitionRepository(beans);
        validator.validatePost(repo);
        return new Context(repo);
    }

    private Collection<BeanDefinition<?>> createBeans(Collection<ConfigurationDeclaration> configurations) {
        Collection<BeanDefinition<?>> definitions = new LinkedList<>();
        for (ConfigurationDeclaration configuration : configurations) {
            for (BeanDeclaration declaration : configuration.getBeans()) {
                definitions.add(createBeanDefinition(declaration));
            }
        }
        return definitions;
    }

    private BeanDefinition<?> createBeanDefinition(BeanDeclaration declaration) {
        Constructor constructor = analyzer.findProperConstructor(declaration.getBeanType());
        Collection<MetaClass> dependencies = analyzer.findDependencies(constructor);

        Collection<MetaClass> registrationTypes = declaration.getRegistrationTypes();
        if (declaration.getScope() == Scope.SINGLETON) {
            // TODO seperate interface type in second arg ;)
            return new SingletonBeanDefinition(declaration.getBeanType(), registrationTypes, constructor, dependencies);
        }
        return new BeanDefinition(declaration.getBeanType(), registrationTypes, constructor, dependencies);
    }

}
