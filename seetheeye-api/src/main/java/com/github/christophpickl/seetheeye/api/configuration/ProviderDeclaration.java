package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;

public class ProviderDeclaration implements Declaration {

    private final MetaClass/*Provider<?>*/ providerType;

    public ProviderDeclaration(Class<? extends Provider<?>> providerType) {
        this.providerType = new MetaClass(providerType);
    }

    @Override
    public MetaClass getInstallType() {
        return providerType;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypes() {
        return Arrays.asList(providerType.getSingleTypeParamaterOfSingleInterface());
    }
}
