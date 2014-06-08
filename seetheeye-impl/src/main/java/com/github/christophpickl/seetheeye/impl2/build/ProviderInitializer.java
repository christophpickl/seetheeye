package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.Resolver;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderBeanDefinition;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderDefinition;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderInitDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ProviderInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderInitializer.class);

    private final Collection<ProviderDefinition<?>> providerDefinition = new LinkedList<>();
    private final Collection<ProviderBeanDefinition<?>> providerBeanDefinition = new LinkedList<>();

    public void init(Resolver resolver) {
        Map<MetaClass, Provider<?>> instanceByProvidee = createProviderInstancesByProvideeTypeMapping(resolver);

        providerDefinition.forEach(d -> initProvider(instanceByProvidee, d, d.getInstallType()));
        providerBeanDefinition.forEach(d -> initProvider(instanceByProvidee, d, d.getProvideeType()));
    }

    private void initProvider(Map<MetaClass, Provider<?>> instanceByProvidee, ProviderInitDefinition<?, Provider<?>> definition, MetaClass provideeType) {
        Provider<?> provider = instanceByProvidee.get(provideeType);
        if (provider == null) {
            throw new RuntimeException("Woah! Could not find provider for providee type: " + provideeType.getName());
        }
        definition.initProvider(provider);
    }

    private Map<MetaClass, Provider<?>> createProviderInstancesByProvideeTypeMapping(Resolver resolver) {
        Map<MetaClass, Provider<?>> providerInstancesByProvideeType = new HashMap<>();

        providerDefinition.forEach(definition -> {
            InstantiatonTemplate<? extends Provider<?>> template = definition.getProviderTemplate();
            Collection<Object> arguments = resolver.createArguments(template.getDependencies());
            Provider<?> providerInstance = template.instantiate(arguments);
            providerInstancesByProvideeType.put(definition.getInstallType(), providerInstance);
        });

        return providerInstancesByProvideeType;
    }

    public void addAllProviders(Collection<ProviderDefinition<?>> definitions) {
        providerDefinition.addAll(definitions);
    }

    public void addAllProviderBeans(Collection<ProviderBeanDefinition<?>> definitions) {
        providerBeanDefinition.addAll(definitions);
    }

}
