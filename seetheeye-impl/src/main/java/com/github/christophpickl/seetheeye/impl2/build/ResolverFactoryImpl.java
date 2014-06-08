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
        ProviderInitializer providerInitializer = new ProviderInitializer();

        for (ConfigurationDeclaration configuration : configurations) {
            definitions.addAll(createBeans(configuration.getBeans()));
            definitions.addAll(createInstances(configuration.getInstances()));

            Collection<ProviderDefinition<?>> perConfigurationProviderDefinition = createProviders(configuration.getProviders());
            Collection<ProviderBeanDefinition<?>> perConfigurationProviderBeanDefinition =
                perConfigurationProviderDefinition.stream()
                    // additionally install for each providee type, a provider bean type.
                    .map(d -> {
                        // TODO weeeh, static cast :(
                        InstantiatonTemplate<Provider<?>> template = (InstantiatonTemplate<Provider<?>>) d.getProviderTemplate();
                        return new ProviderBeanDefinition<>(template.getType(), d.getInstallType(), template.getDependencies());
                    }).collect(Collectors.toList());
            definitions.addAll(perConfigurationProviderDefinition);
            definitions.addAll(perConfigurationProviderBeanDefinition);
            providerInitializer.addAllProviders(perConfigurationProviderDefinition);
            providerInitializer.addAllProviderBeans(perConfigurationProviderBeanDefinition);
        }

        DefinitionRepository repo = new DefinitionRepository(definitions);
        validator.validatePost(repo);
        Resolver resolver = new Resolver(repo);
        providerInitializer.init(resolver);
        return resolver;
    }


    private void postInit(Resolver resolver) {
        // FIXME implement me: eager instantiate singeltons
    }

    private Collection<BeanDefinition<?>> createBeans(Collection<BeanDeclaration> beans) {
        return beans.stream().map(this::createBeanDefinition).collect(Collectors.toList());
    }

    private Collection<InstanceDefinition<?>> createInstances(Collection<InstanceDeclaration> instances) {
        return instances.stream().map(
                declaration -> new InstanceDefinition<>(declaration.getInstance(), declaration.getRegistrationTypes()))
                .collect(Collectors.toList());
    }

    private Collection<ProviderDefinition<?>> createProviders(Collection<ProviderDeclaration> declarations) {
        Collection<ProviderDefinition<?>> definitions = new LinkedList<>();
        for (ProviderDeclaration declaration : declarations) {
            MetaClass installType = declaration.getInstallType();
            Constructor constructor = analyzer.findProperConstructor(installType);
            Collection<MetaClass> dependencies = analyzer.findDependencies(constructor);
            MetaClass provideeType = installType.getSingleTypeParamaterOfSingleInterface();
            ProviderDefinition<Object> providerDefinition = new ProviderDefinition<>(
                new InstantiatonTemplate(installType, constructor, dependencies), provideeType);
            definitions.add(providerDefinition);
        }
        return definitions;
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
