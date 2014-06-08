package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ProviderDeclaration;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.github.christophpickl.seetheeye.impl2.configuration.Definition;
import com.github.christophpickl.seetheeye.impl2.configuration.DefinitionRepository;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderBeanDefinition;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderDefinition;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ResolverFactoryImpl implements ResolverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ResolverFactoryImpl.class);

    private final ConfigurationValidator validator;

    private final BeanDefinitionMapper beanMapper;
    private final InstanceDefinitionMapper instanceMapper;
    private final ProviderDefinitionMapper providerMapper;

    @Inject
    public ResolverFactoryImpl(ConfigurationValidator validator, BeanDefinitionMapper beanMapper,
                               InstanceDefinitionMapper instanceMapper, ProviderDefinitionMapper providerMapper) {
        this.validator = validator;
        this.beanMapper = beanMapper;
        this.instanceMapper = instanceMapper;
        this.providerMapper = providerMapper;
    }


    public Resolver create(Collection<ConfigurationDeclaration> configurations) {
        LOG.debug("create(configurations.size={})", configurations.size());
        validator.validatePre(configurations);

        Collection<Definition<?>> definitions = new LinkedList<>();
        ProviderInitializer providerInitializer = new ProviderInitializer();

        for (ConfigurationDeclaration configuration : configurations) {
            definitions.addAll(beanMapper.map(configuration.getBeans()));
            definitions.addAll(instanceMapper.map(configuration.getInstances()));

            createProviders(definitions, providerInitializer, configuration.getProviders());
        }

        DefinitionRepository repository = new DefinitionRepository(definitions);
        validator.validatePost(repository);
        Resolver resolver = new Resolver(repository);
        providerInitializer.init(resolver);
        return resolver;
    }

    private void createProviders(Collection<Definition<?>> definitions, ProviderInitializer initializer, Collection<ProviderDeclaration> declarations) {
        LOG.trace("createProviders(definitions, initializer, declarations)");
        Collection<ProviderDefinition<?>> perConfigurationProviderDefinition = providerMapper.map(declarations);

        Collection<ProviderBeanDefinition<?>> perConfigurationProviderBeanDefinition =
            perConfigurationProviderDefinition.stream()
                // additionally install for each providee type, a provider bean type.
                .map(d -> {
                    // TODO weeeh, static cast :(
                    InstantiatonTemplate<Provider<?>> template = (InstantiatonTemplate<Provider<?>>) d.getProviderTemplate();
                    ProviderBeanDefinition<Provider<?>> definition = new ProviderBeanDefinition<>(template.getType(), d.getInstallType(), template.getDependencies());
                    LOG.debug("Created provider bean definition: {}", definition);
                    return definition;
                }).collect(Collectors.toList());

        definitions.addAll(perConfigurationProviderDefinition);
        definitions.addAll(perConfigurationProviderBeanDefinition);

        initializer.addProviders(perConfigurationProviderDefinition);
        initializer.addProviderBeans(perConfigurationProviderBeanDefinition);
    }


    private void postInit(Resolver resolver) {
        // FIXME implement me: eager instantiate singeltons
    }



}
