package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.InstanceDeclaration;
import com.github.christophpickl.seetheeye.impl2.configuration.InstanceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public class InstanceDefinitionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceDefinitionMapper.class);

    public Collection<InstanceDefinition<?>> map(Collection<InstanceDeclaration> declarations) {
        return declarations.stream().map(declaration -> {
            InstanceDefinition<Object> definition = new InstanceDefinition<>(declaration.getInstance(), declaration.getRegistrationTypes());
            LOG.debug("Created instance definition: {}", definition);
            return definition;
        }).collect(Collectors.toList());
    }


}
