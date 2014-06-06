package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Context {

    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private final DefinitionRepository repo;

    public Context(DefinitionRepository repo) {
        this.repo = repo;
    }

    public <T> T get(Class<T> beanType) {
        LOG.debug("get(beanType={})", beanType.getName());
        if (!repo.isRegistered(beanType)) {
            throw new SeeTheEyeException.UnresolvableBeanException(beanType);
        }
        DefinitionX bean = repo.lookupRegistered(beanType);
        return (T) bean.instance(createArguments(bean.getDependencies()));
    }

    private Collection<Object> createArguments(Collection<MetaClass> dependencies) {
        return dependencies.stream().map(d -> (Object)get(d.getEnclosedClass())).collect(Collectors.toCollection(LinkedList::new));
    }
}
