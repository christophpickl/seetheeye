package com.github.christophpickl.seetheeye.api.configuration;

import java.util.Collection;

public class ConfigurationDeclaration {

    private final AbstractConfiguration originalUserConfiguration;
    private final Collection<BeanDeclaration> beans;
    private final Collection<InstanceDeclaration> instances;

    public ConfigurationDeclaration(AbstractConfiguration originalUserConfiguration,
                                    Collection<BeanDeclaration> beans, Collection<InstanceDeclaration> instances) {
        this.originalUserConfiguration = originalUserConfiguration;
        this.beans = beans;
        this.instances = instances;
    }

    public final AbstractConfiguration getOriginalUserConfiguration() {
        return originalUserConfiguration;
    }

    public final Collection<BeanDeclaration> getBeans() {
        return beans;
    }


    public Collection<InstanceDeclaration> getInstances() {
        return instances;
    }
}
