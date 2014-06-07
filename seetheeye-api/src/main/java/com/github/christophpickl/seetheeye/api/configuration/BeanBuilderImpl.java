package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.build.BeanBuilder;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BeanBuilderImpl implements BeanBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BeanBuilderImpl.class);

    private final BeanDeclaration declaration;

    BeanBuilderImpl(BeanDeclaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public BeanBuilder in(Scope scope) {
        Preconditions.checkNotNull(scope);
        LOG.trace("in(scope={})", scope);
        declaration.setScope(scope);
        return this;
    }

    @Override
    public BeanBuilder as(Class<?> interfaceType) {
        Preconditions.checkNotNull(interfaceType);
        LOG.trace("as(interfaceType={})", interfaceType);
        declaration.addRegistrationType(new MetaClass(interfaceType));
        return this;
    }

}
