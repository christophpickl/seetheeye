package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.impl2.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ContextFactoryImpl implements ContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ContextFactoryImpl.class);

    public Context create(Collection<ConfigurationDefinition> definitions) {
        LOG.debug("create(definitions)");
        return new Context();
    }
}
