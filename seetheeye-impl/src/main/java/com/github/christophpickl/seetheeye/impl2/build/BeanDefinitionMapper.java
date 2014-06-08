package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.Scope;
import com.github.christophpickl.seetheeye.impl2.configuration.BeanDefinition;
import com.github.christophpickl.seetheeye.impl2.configuration.SingletonBeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.stream.Collectors;

public class BeanDefinitionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BeanDefinitionMapper.class);

    private final TypeAnalyzer analyzer;

    @Inject
    public BeanDefinitionMapper(TypeAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Collection<BeanDefinition<?>> map(Collection<BeanDeclaration> declarations) {
        return declarations.stream().map(declaration -> {
            Constructor constructor = analyzer.findProperConstructor(declaration.getInstallType());
            Collection<MetaClass> dependencies = analyzer.findDependencies(constructor);

            Collection<MetaClass> registrationTypes = declaration.getRegistrationTypes();
            BeanDefinition<?> definiton;
            if (declaration.getScope() == Scope.SINGLETON) {
                // TODO seperate interface type in second arg ;)
                definiton = new SingletonBeanDefinition(declaration.getInstallType(), registrationTypes, constructor, dependencies);
            } else {
                definiton = new BeanDefinition(declaration.getInstallType(), registrationTypes, constructor, dependencies);
            }
            LOG.debug("Created bean definition: {}", definiton);
            return definiton;
        }).collect(Collectors.toList());
    }
}
