package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

public class BeanAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(BeanAnalyzer.class);

    public Constructor findConstructor(Class<?> beanType) {
        LOG.trace("findConstructor(beanType={})", beanType);

        Constructor[] constructors = beanType.getDeclaredConstructors();

        if (constructors.length == 1) {
            LOG.trace("Found only single existing constructor with parameters: [{}]",
                ReflectionUtil.paramsToString(constructors[0]));
            return constructors[0];
        }
        findInjectConstructor(beanType);

        throw new SeeTheEyeException.InvalidBeanException("Invalid bean type: " + beanType.getName() + "! " +
                "Not found @Inject on any constructor, nor is the default constructor existing!");
    }

    private Optional<Constructor> findInjectConstructor(Class<?> beanType) {
        Collection<Constructor> foundConstructors = new LinkedList<>();
        for (Constructor constructor : beanType.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                LOG.trace("Found constructor with @Inject on it: '{}'", constructor);
                foundConstructors.add(constructor);
            }
        }
        if (foundConstructors.size() == 0) {
            return Optional.empty();
        }
        if (foundConstructors.size() == 1) {
            return Optional.of(foundConstructors.iterator().next());
        }
        throw new SeeTheEyeException.InvalidBeanException(
                "Multiple constructors found annotated with @Inject for type: " + beanType.getName());
    }

}
