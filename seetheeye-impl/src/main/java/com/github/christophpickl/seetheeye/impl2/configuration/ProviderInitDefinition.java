package com.github.christophpickl.seetheeye.impl2.configuration;

import javax.inject.Provider;

public interface ProviderInitDefinition<X, T extends Provider<?>> extends Definition<X> {

    void initProviderInstance(T provider);

}
