package com.github.christophpickl.seetheeye.api.configuration;

import java.util.Collection;

public class ConfigurationDeclaration {

    private final AbstractConfiguration originalUserConfiguration;
    private final Collection<BeanDeclaration> beans;

    public ConfigurationDeclaration(AbstractConfiguration originalUserConfiguration, Collection<BeanDeclaration> beans) {
        this.originalUserConfiguration = originalUserConfiguration;
        this.beans = beans;
    }

    public final AbstractConfiguration getOriginalUserConfiguration() {
        return originalUserConfiguration;
    }

    public final Collection<BeanDeclaration> getBeans() {
        return beans;
    }

}
