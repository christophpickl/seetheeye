package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.build.InstanceBuilder;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InstanceBuilderImpl implements InstanceBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceBuilderImpl.class);

    private final InstanceDeclaration declaration;

    InstanceBuilderImpl(InstanceDeclaration declaration) {
        this.declaration = Preconditions.checkNotNull(declaration);
    }

    @Override
    public InstanceBuilder as(Class<?> interfaceType) {
        LOG.trace("as(interfaceType={})", interfaceType);
        declaration.addRegistrationType(new MetaClass(interfaceType));
        return this;
    }

}
