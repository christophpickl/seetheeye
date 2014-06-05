package com.github.christophpickl.seetheeye.api;

import java.util.Collection;

public class ConfigurationDefinition {

    private final AbstractConfiguration originalUserConfiguration;
    private final Collection<BeanDefinition> beans;

    public ConfigurationDefinition(AbstractConfiguration originalUserConfiguration, Collection<BeanDefinition> beans) {
        this.originalUserConfiguration = originalUserConfiguration;
        this.beans = beans;
    }

    public final AbstractConfiguration getOriginalUserConfiguration() {
        return originalUserConfiguration;
    }

    public final Collection<BeanDefinition> getBeans() {
        return beans;
    }

}
