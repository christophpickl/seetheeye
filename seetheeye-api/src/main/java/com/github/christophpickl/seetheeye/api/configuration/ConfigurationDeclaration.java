package com.github.christophpickl.seetheeye.api.configuration;

import java.util.Collection;

public class ConfigurationDeclaration {

    private final Collection<BeanDeclaration> beans;
    private final Collection<InstanceDeclaration> instances;
    private final Collection<ProviderDeclaration> providers;

    public ConfigurationDeclaration(Collection<BeanDeclaration> beans, Collection<InstanceDeclaration> instances, Collection<ProviderDeclaration> providers) {
        this.beans = beans;
        this.instances = instances;
        this.providers = providers;
    }

    public final Collection<BeanDeclaration> getBeans() {
        return beans;
    }

    public Collection<InstanceDeclaration> getInstances() {
        return instances;
    }

    public Collection<ProviderDeclaration> getProviders() {
        return providers;
    }
}
