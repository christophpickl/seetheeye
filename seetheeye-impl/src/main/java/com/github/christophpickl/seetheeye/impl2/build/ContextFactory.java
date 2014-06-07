package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.impl2.Context;

import java.util.Collection;

public interface ContextFactory {

    Context create(Collection<ConfigurationDeclaration> declarations);

}
