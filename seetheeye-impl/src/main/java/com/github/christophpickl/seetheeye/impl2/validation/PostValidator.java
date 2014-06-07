package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.configuration.Definition;
import com.github.christophpickl.seetheeye.impl2.configuration.DefinitionRepository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

class PostValidator {

    private final DefinitionRepository repo;
    private final Collection<String> errors = new LinkedList<>();
    private final Set<Definition<?>> markedBeans = new LinkedHashSet<>();

    PostValidator(DefinitionRepository repo) {
        this.repo = repo;
    }

    public Collection<String> detect() {
        repo.getDefinitions().forEach(this::recursivelyCheckForCyclicDependency);
        return errors;
    }

    private void recursivelyCheckForCyclicDependency(Definition definition) {
        if (markedBeans.contains(definition)) {
            errors.add("Found cyclic dependency for bean: " + definition.getInstallType().getName());
            return;
        }
        markedBeans.add(definition);

        Collection<MetaClass> dependencies = definition.getDependencies();
        for (MetaClass dependency : dependencies) {
            if (!repo.isRegistered(dependency.getEnclosedClass())) {
                errors.add("Unresolved dependency of type " + dependency.getName() +
                        " for bean: " + definition.getInstallType());
                continue;
            }
            Definition dependencyDefinition = repo.lookupRegistered(dependency.getEnclosedClass());
            recursivelyCheckForCyclicDependency(dependencyDefinition);
        }

        markedBeans.remove(definition);
    }

}
