package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.BeanDefinition;
import com.github.christophpickl.seetheeye.api.ConfigurationDefinition;
import com.github.christophpickl.seetheeye.impl2.BeanDefinitionX;
import com.github.christophpickl.seetheeye.impl2.Context;
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

        Collection<BeanDefinitionX> allBeans = new LinkedList<>();
        for (ConfigurationDefinition definition : definitions) {
            Collection<BeanDefinition> beans = definition.getBeans();
            for (BeanDefinition bean : beans) {
                Constructor constructor = analyzer.findConstructor(bean.getBeanType());
                allBeans.add(new BeanDefinitionX(bean.getBeanType(), constructor));
            }
        }
        return new Context(allBeans);
    }

}
