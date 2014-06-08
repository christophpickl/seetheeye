package com.github.christophpickl.seetheeye.api.configuration;

import java.util.Collection;
import java.util.LinkedList;

public class ConfigDeclarationBuilder {

    private final Collection<BeanDeclaration> beans = new LinkedList<>();
    private final Collection<InstanceDeclaration> instances = new LinkedList<>();
    private final Collection<ProviderDeclaration> providers = new LinkedList<>();

    public ConfigDeclarationBuilder addBean(BeanDeclaration bean) {
        beans.add(bean);
        return this;
    }

    public ConfigDeclarationBuilder addInstance(InstanceDeclaration instance) {
        instances.add(instance);
        return this;
    }

    public ConfigDeclarationBuilder addProvider(ProviderDeclaration provider) {
        providers.add(provider);
        return this;
    }

    public ConfigurationDeclaration build() {
        return new ConfigurationDeclaration(beans, instances, providers);
    }
}
