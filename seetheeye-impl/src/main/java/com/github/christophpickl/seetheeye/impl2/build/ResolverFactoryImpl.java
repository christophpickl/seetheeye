package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.*;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.github.christophpickl.seetheeye.impl2.configuration.*;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ResolverFactoryImpl implements ResolverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ResolverFactoryImpl.class);

    private final BeanAnalyzer analyzer;
    private final ConfigurationValidator validator;

    @Inject
    public ResolverFactoryImpl(BeanAnalyzer analyzer, ConfigurationValidator validator) {
        this.analyzer = analyzer;
        this.validator = validator;
    }


    public Resolver create(Collection<ConfigurationDeclaration> configurations) {
        LOG.debug("create(configurations)");
        validator.validatePre(configurations);

        Collection<Definition<?>> definitions = new LinkedList<>();
        for (ConfigurationDeclaration configuration : configurations) {
            definitions.addAll(createBeans(configuration.getBeans()));
            definitions.addAll(createInstances(configuration.getInstances()));
            definitions.addAll(createProviders(configuration.getProviders()));
        }
        DefinitionRepository repo = new DefinitionRepository(definitions);
        validator.validatePost(repo);
        return new Resolver(repo);
    }

    private Collection<BeanDefinition<?>> createBeans(Collection<BeanDeclaration> beans) {
        return beans.stream().map(this::createBeanDefinition).collect(Collectors.toList());
    }

    private Collection<InstanceDefinition<?>> createInstances(Collection<InstanceDeclaration> instances) {
        return instances.stream().map(
                declaration -> new InstanceDefinition<>(declaration.getInstance(), declaration.getRegistrationTypes()))
                .collect(Collectors.toList());
    }

    private Collection<Definition<?>> createProviders(Collection<ProviderDeclaration> providers) {
        return providers.stream().map(declaration -> {
            MetaClass installType = declaration.getInstallType();
            // FIXME dependencies for provider have to be saved but... dependencies can only be resolved a little bit later :(
            Constructor constructor = analyzer.findProperConstructor(installType);
            Provider<Object> provider = (Provider<Object>) installType.instantiate(constructor);
            MetaClass provideeType = installType.getSingleTypeParamaterOfSingleInterface();
            return new ProviderDefinition<>(provider, provideeType);
        }).collect(Collectors.toList());
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
