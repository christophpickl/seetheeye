package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;

public interface DefinitionX<T> {

    MetaClass getInstallType();

    MetaClass getRegistrationType();

    Collection<MetaClass> getDependencies();

    T instance(Collection<Object> arguments);

}
