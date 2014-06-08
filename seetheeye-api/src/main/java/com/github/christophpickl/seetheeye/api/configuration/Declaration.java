package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.util.Collection;

public interface Declaration {

    /**
     * User installed type in case of:
     *
     * A) installBean: concrete bean class (if no as-interface/registration type is defined)
     * B) installInstance: instance runtime type
     * C) installProvider: provider type itself (not the provided type)
     *
     * Will be used for bean type lookup in DefinitionRepository which is the exact type of the user's requested type.
     */
    MetaClass getInstallType();

    /**
     * Lookup type in case of:
     *
     * A) installBean: as-interfaces
     * B) installInstance: as-interfaces
     * C) installProvider: FIXME add javadoc (should be provider type and providee type? maybe let configure even super types ;)
     *
     * Will be used for bean type lookup in DefinitionRepository which is the exact type of the user's requested type.
     */
    Collection<MetaClass> getRegistrationTypes();

}
