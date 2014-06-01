package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

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
        validate();
        Collection<Bean> beans = new LinkedHashSet<>();
        for (AbstractConfig config : configs) {
            beans.addAll(config.getInstalledBeans());
        }
        return new SeeTheEye(beans);
    }

    private void validate() {
        Collection<Class<?>> installedBeanTypes = new HashSet<>();
        Collection<Class<?>> installedBeanInterfaces = new HashSet<>();
        for (AbstractConfig config : configs) {
            for (Bean bean : config.getInstalledBeans()) {
                if (bean.getBeanInterface().isPresent()) {
                    Class<?> interfase = bean.getBeanInterface().get();
                    if (installedBeanInterfaces.contains(interfase)) {
                        throw new SeeTheEyeException.ConfigInvalidException("Duplicate bean interface registration for: " + interfase.getName() + "!");
                    }
                    installedBeanInterfaces.add(interfase);

                } else {
                    MetaClass type = bean.getBeanType();
                    if (installedBeanTypes.contains(type.getClazz())) {
                        throw new SeeTheEyeException.ConfigInvalidException("Duplicate bean type registration for: " + type.getName() + "!");
                    }
                    installedBeanTypes.add(type.getClazz());
                }
            }
        }
    }

}
