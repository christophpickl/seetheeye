package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.impl2.DefinitionRepository;
import com.github.christophpickl.seetheeye.impl2.DefinitionX;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

class PostDependencyCycleDetector {

    private final DefinitionRepository repo;
    private final Collection<String> errors = new LinkedList<>();
    private final Set<DefinitionX<?>> markedBeans = new LinkedHashSet<>();

    PostDependencyCycleDetector(DefinitionRepository repo) {
        this.repo = repo;
    }

    public Collection<String> detect() {
        repo.getBeans().forEach(this::recursivelyCheckDefinition);
        return errors;
    }

    private void recursivelyCheckDefinition(DefinitionX definition) {
        if (markedBeans.contains(definition)) {
            errors.add("Found cyclic dependency for bean: " + definition.getRegistrationType().getName());
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
            DefinitionX dependencyDefinition = repo.lookupRegistered(dependency.getEnclosedClass());
            recursivelyCheckDefinition(dependencyDefinition);
        }
        markedBeans.remove(definition);
    }

}
