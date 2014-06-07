package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.build.BeanBuilder;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BeanBuilderImpl implements BeanBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BeanBuilderImpl.class);

    private final BeanDeclaration bean;

    BeanBuilderImpl(BeanDeclaration bean) {
        this.bean = bean;
    }

    @Override
    public BeanBuilder in(Scope scope) {
        Preconditions.checkNotNull(scope);
        LOG.trace("in(scope={})", scope);
        bean.setScope(scope);
        return this;
    }

    @Override
    public BeanBuilder as(Class<?> interfaceType) {
        Preconditions.checkNotNull(interfaceType);
        LOG.trace("as(interfaceType={})", interfaceType);
        bean.addRegistrationType(new MetaClass(interfaceType));
        return this;
    }

}
