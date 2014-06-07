package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;

public interface Definition<T> {

    MetaClass getInstallType();

    Collection<MetaClass> getRegistrationTypesOrInstallType();

    Collection<MetaClass> getDependencies();

    T instance(Collection<Object> arguments);

}
