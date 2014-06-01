package com.github.christophpickl.seetheeye.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final Collection<AbstractConfig> configs = new HashSet<>();

    SeeTheEyeBuilder() {}

    public SeeTheEyeBuilder configs(AbstractConfig config, AbstractConfig... evenMore) {
        addConfig(config);
        for (AbstractConfig more : evenMore) {
            addConfig(more);
        }
        return this;
    }

    private void addConfig(AbstractConfig config) {
        LOG.debug("Installing config: {}", config.getClass().getName());
        configs.add(config);
    }


    public SeeTheEye build() {
        Collection<Bean> beans = new LinkedHashSet<>();
        for (AbstractConfig config : configs) {
            beans.addAll(config.getInstalledBeans());
        }
        return new SeeTheEye(beans);
    }
}
