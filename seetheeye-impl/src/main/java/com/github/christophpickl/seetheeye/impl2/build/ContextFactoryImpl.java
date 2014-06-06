package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.api.Scope;
import com.github.christophpickl.seetheeye.impl2.BeanDefinitionX;
import com.github.christophpickl.seetheeye.impl2.Context;
import com.github.christophpickl.seetheeye.impl2.SingletonBeanDefinitionX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;

public class ContextFactoryImpl implements ContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ContextFactoryImpl.class);

    private final BeanAnalyzer analyzer;
    private final BeanValidator validator;

    @Inject
    public ContextFactoryImpl(BeanAnalyzer analyzer, BeanValidator validator) {
        this.analyzer = analyzer;
        this.validator = validator;
    }


    public Context create(Collection<ConfigurationDefinition> definitions) {
        LOG.debug("create(definitions)");
        validator.validate(definitions);

        return new Context(createBeans(definitions));
    }

    private Collection<BeanDefinitionX> createBeans(Collection<ConfigurationDefinition> definitions) {
        Collection<BeanDefinitionX> beans = new LinkedList<>();
        for (ConfigurationDefinition definition : definitions) {
            for (BeanDefinition bean : definition.getBeans()) {
                Constructor constructor = analyzer.findProperConstructor(bean.getBeanType());
                BeanDefinitionX definitionX;
                if (bean.getScope() == Scope.SINGLETON) {
                    definitionX = new SingletonBeanDefinitionX(bean.getBeanType(), constructor);
                } else {
                    definitionX = new BeanDefinitionX(bean.getBeanType(), constructor);
                }
                beans.add(definitionX);
            }
        }
        return beans;
    }

}
