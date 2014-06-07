package com.github.christophpickl.seetheeye.api.configuration;

import java.util.Collection;

public class ConfigurationDeclaration {

    private final Collection<BeanDeclaration> beans;
    private final Collection<InstanceDeclaration> instances;

    public ConfigurationDeclaration(Collection<BeanDeclaration> beans, Collection<InstanceDeclaration> instances) {
        this.beans = beans;
        this.instances = instances;
    }

    public final Collection<BeanDeclaration> getBeans() {
        return beans;
    }

    public Collection<InstanceDeclaration> getInstances() {
        return instances;
    }

}
