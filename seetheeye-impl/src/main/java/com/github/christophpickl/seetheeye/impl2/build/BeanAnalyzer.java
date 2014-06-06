package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(BeanAnalyzer.class);

    public Constructor findProperConstructor(MetaClass beanType) {
        LOG.trace("findProperConstructor(beanType={})", beanType);

        if (beanType.hasOnlySingleConstructor()) {
            Constructor singleConstructor = beanType.getSingleConstructor();
            LOG.trace("Found only single existing constructor with parameters: [{}]",
                ReflectionUtil.paramsToString(singleConstructor));
            return singleConstructor;
        }
        Optional<Constructor> foundInjectConstructor = findInjectConstructor(beanType);
        if (foundInjectConstructor.isPresent()) {
            return foundInjectConstructor.get();
        }

        throw new SeeTheEyeException.InvalidBeanException("Invalid bean type: " + beanType.getName() + "! " +
                "Not found @Inject on any constructor, nor is the default constructor existing!");
    }

    private Optional<Constructor> findInjectConstructor(MetaClass beanType) {
        Collection<Constructor> foundConstructors = beanType.getDeclaredConstructorsAnnotatedWith(Inject.class);
        if (foundConstructors.size() == 0) {
            return Optional.empty();
        }
        if (foundConstructors.size() == 1) {
            return Optional.of(foundConstructors.iterator().next());
        }
        throw new SeeTheEyeException.InvalidBeanException(
                "Multiple constructors found annotated with @Inject for type: " + beanType.getName());
    }

    public Collection<MetaClass> findDependencies(Constructor constructor) {
        return Arrays.asList(constructor.getParameterTypes()).stream()
            .map(p -> new MetaClass(p))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
