package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.impl2.Resolver;

import java.util.Collection;

public interface ResolverFactory {

    Resolver create(Collection<ConfigurationDeclaration> declarations);

}
