package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefinitionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DefinitionRepository.class);

    private final Map<Class<?>, Definition<?>> definitionsByRegistrationType = new HashMap<>();

    private final Collection<Definition<?>> definitions;

    public DefinitionRepository(Collection<Definition<?>> definitions) {
        this.definitions = definitions;
        for (Definition<?> bean : definitions) {
            for (MetaClass registrationType : bean.getRegistrationTypesOrInstallType()) {
                LOG.trace("Registering " + bean.getInstallType().getName() + " as type: " + registrationType.getName());
                definitionsByRegistrationType.put(registrationType.getEnclosedClass(), bean);
            }
        }
    }

    public Collection<Definition<?>> getDefinitions() {
        return definitions;
    }

    public <T> boolean isRegistered(Class<T> beanType) {
        return definitionsByRegistrationType.containsKey(beanType);
    }

    public <T> Definition lookupRegistered(Class<T> beanType) {
        if (!isRegistered(beanType)) throw new IllegalStateException("Not registered bean: " + beanType.getName());
        return definitionsByRegistrationType.get(beanType);
    }

}
