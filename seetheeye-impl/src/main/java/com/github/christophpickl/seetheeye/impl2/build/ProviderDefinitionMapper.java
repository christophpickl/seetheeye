package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.ProviderDeclaration;
import com.github.christophpickl.seetheeye.impl2.configuration.ProviderDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProviderDefinitionMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderDefinitionMapper.class);

    private final TypeAnalyzer analyzer;

    @Inject
    public ProviderDefinitionMapper(TypeAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Collection<ProviderDefinition<?>> map(Collection<ProviderDeclaration> declarations) {
        return declarations.stream().map(declaration -> {
            MetaClass installType = declaration.getInstallType();
            Constructor constructor = analyzer.findProperConstructor(installType);
            Collection<MetaClass> dependencies = analyzer.findDependencies(constructor);
            MetaClass provideeType = installType.getSingleTypeParamaterOfSingleInterface();
            ProviderDefinition<Object> providerDefinition = new ProviderDefinition<>(
                new InstantiatonTemplate(installType, constructor, dependencies), provideeType);
            LOG.debug("Created provider definition: {}", providerDefinition);
            return providerDefinition;
        }).collect(Collectors.toList());
    }

}
