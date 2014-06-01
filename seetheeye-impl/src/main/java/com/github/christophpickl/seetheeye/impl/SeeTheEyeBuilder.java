package com.github.christophpickl.seetheeye.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final Collection<Config> configs = new HashSet<>();

    SeeTheEyeBuilder() {}

    public SeeTheEyeBuilder configs(Config config, Config... evenMore) {
        addConfig(config);
        for (Config more : evenMore) {
            addConfig(more);
        }
        return this;
    }

    private void addConfig(Config config) {
        LOG.debug("Installing config: {}", config.getClass().getName());
        configs.add(config);
    }


    public SeeTheEye build() {
        Collection<Bean> beans = new LinkedHashSet<>();
        for (Config config : configs) {
            beans.addAll(config.getInstalledBeans());
        }
        return new SeeTheEye(beans);
    }
}
