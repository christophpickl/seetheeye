package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.configuration.Definition;
import com.github.christophpickl.seetheeye.impl2.configuration.DefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Resolver {

    private static final Logger LOG = LoggerFactory.getLogger(Resolver.class);

    private final DefinitionRepository repo;

    public Resolver(DefinitionRepository repo) {
        this.repo = repo;
    }

    public <T> T get(Class<T> beanType) {
        LOG.info("get(beanType={})", beanType.getName());
        if (!repo.isRegistered(beanType)) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }
        Definition<T> bean = repo.lookupRegistered(beanType);
        return bean.instanceEagerOrLazyIDontCare(this);
//      OLD implementation: return (T) bean.instance(createArguments(bean.getDependencies()));
    }

    public Collection<Object> createArguments(Collection<MetaClass> dependencies) {
        LOG.trace("createArguments(dependencies={})", Arrays.toString(dependencies.toArray()));
        // (Object) cast IS necessary! dont delete it.
        return dependencies.stream().map(d -> (Object) get(d.getEnclosedClass())).collect(Collectors.toCollection(LinkedList::new));
    }

}
