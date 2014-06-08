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
        LOG.info("init(resolver)");
        Map<MetaClass, Provider<?>> instanceByProvidee = createProviderInstanceMapping(resolver);

        providerDefinition.forEach(d -> initProvider(instanceByProvidee, d, d.getInstallType()));
//        providerBeanDefinition.forEach(d -> initProvider(instanceByProvidee, d, d.getProvideeType()));
    }

    private void initProvider(Map<MetaClass, Provider<?>> instanceByProvidee, ProviderInitDefinition<?, Provider<?>> definition, MetaClass provideeType) {
        LOG.trace("initProviderInstance(instanceByProvidee, definition={}, provideeType.name={})", definition, provideeType.getName());
        Provider<?> provider = instanceByProvidee.get(provideeType);
        if (provider == null) {
            throw new RuntimeException("Woah! Could not find provider for providee type: " + provideeType.getName());
        }
        definition.initProviderInstance(provider);
    }

    private Map<MetaClass, Provider<?>> createProviderInstanceMapping(Resolver resolver) {
        Map<MetaClass, Provider<?>> providerInstancesByProvideeType = new HashMap<>();

        Map<MetaClass, ProviderBeanDefinition<?>> providerDefinitionByProvideeType = new HashMap<>();
        providerBeanDefinition.forEach(d -> providerDefinitionByProvideeType.put(d.getProvideeType(), d));

        providerDefinition.forEach(definition -> {
            InstantiatonTemplate<? extends Provider<?>> template = definition.getProviderTemplate();
            LOG.trace("Creating provider instance {} (providee install type: {}).",
                template.getType().getName(), definition.getInstallType().getName());
            Collection<Object> arguments = resolver.createArguments(template.getDependencies());
            Provider<?> providerInstance = template.instantiate(arguments);
            ProviderBeanDefinition<?> foundProviderBean = providerDefinitionByProvideeType.get(definition.getInstallType());
            if (foundProviderBean != null) {
                foundProviderBean.initProviderInstance(providerInstance);
            }
            providerInstancesByProvideeType.put(definition.getInstallType(), providerInstance);
        });

        return providerInstancesByProvideeType;
    }

    public void addProviders(Collection<ProviderDefinition<?>> definitions) {
        LOG.trace("addProviders(definitions.size={})", definitions.size());
        providerDefinition.addAll(definitions);
    }

    public void addProviderBeans(Collection<ProviderBeanDefinition<?>> definitions) {
        LOG.trace("addProviderBeans(definitions.size={})", definitions.size());
        providerBeanDefinition.addAll(definitions);
    }

}
