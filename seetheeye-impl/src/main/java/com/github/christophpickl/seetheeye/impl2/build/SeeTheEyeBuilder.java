package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.configuration.Configuration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.impl2.SeeTheEye;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final ResolverFactory resolverFactory;
    private final Collection<Configuration> configurations = new LinkedHashSet<>();

    @Inject
    public SeeTheEyeBuilder(ResolverFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    public SeeTheEyeApi build() {
        LOG.info("build()");
        Collection<ConfigurationDeclaration> declarations = configurations.stream()
                .map(Configuration::toDeclaration).collect(Collectors.toList());
        return new SeeTheEye(resolverFactory.create(declarations));
    }

    public SeeTheEyeBuilder add(Configuration config, Configuration... moreConfigs) {
        Collection<Configuration> configsToAdd = Lists.asList(config, moreConfigs);
        LOG.debug("Adding following configuration(s): {}", Arrays.toString(configsToAdd.toArray()));
        configurations.addAll(configsToAdd);
        return this;
    }
}
