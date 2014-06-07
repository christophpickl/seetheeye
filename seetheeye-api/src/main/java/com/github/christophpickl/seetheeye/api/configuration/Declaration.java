package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;

public interface Declaration {

    MetaClass getInstallType();

    Collection<MetaClass> getRegistrationTypes();

}
