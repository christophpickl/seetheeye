package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.AbstractConfiguration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.impl2.SeeTheEye;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final ContextFactory contextFactory;
    private final Collection<AbstractConfiguration> configs = new LinkedHashSet<>();

    @Inject
    public SeeTheEyeBuilder(ContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    public SeeTheEyeApi build() {
        Collection<ConfigurationDeclaration> declarations = new LinkedList<>();
        for (AbstractConfiguration config : configs) {
            ConfigurationDeclaration declaration = config.newDeclaration();
            declarations.add(declaration);
        }
        return new SeeTheEye(contextFactory.create(declarations));
    }

    public SeeTheEyeBuilder add(AbstractConfiguration config, AbstractConfiguration... moreConfigs) {
        Collection<AbstractConfiguration> configsToAdd = Lists.asList(config, moreConfigs);
        LOG.trace("Adding following configurations: {}", Arrays.toString(configsToAdd.toArray()));
        configs.addAll(configsToAdd);
        return this;
    }
}
