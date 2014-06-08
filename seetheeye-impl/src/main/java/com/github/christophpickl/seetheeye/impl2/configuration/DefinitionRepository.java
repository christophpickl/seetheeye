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
        for (Definition<?> definition : definitions) {
            for (MetaClass registrationType : definition.getRegistrationTypesOrInstallType()) {
                LOG.debug("Registering " + definition.getInstallType().getName() + " as type: " + registrationType.getName());
                Definition<?> previousStoredDefinition = definitionsByRegistrationType.put(registrationType.getEnclosedClass(), definition);
                if (previousStoredDefinition != null) {
                    throw new IllegalStateException("Internal error occured due to multiple registration types! Seems as validation does not work properly :( " +
                    "Stored definitions were " + previousStoredDefinition + " and " + definition + ".");
                }
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
