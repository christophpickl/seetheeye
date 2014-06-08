package com.github.christophpickl.seetheeye.impl2.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.Collection;

abstract class AbstractBuildingDefinition<T> implements Definition<T> {

    private final MetaClass installType;

    private final Collection<MetaClass> dependencies;

    AbstractBuildingDefinition(MetaClass installType, Collection<MetaClass> dependencies) {
        this.installType = Preconditions.checkNotNull(installType);
        this.dependencies = Preconditions.checkNotNull(dependencies);
    }

    @Override
    public final MetaClass getInstallType() {
        return installType;
    }

    @Override
    public final Collection<MetaClass> getDependencies() {
        return dependencies;
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("installType", installType)
//            .add("dependencies", dependencies)
            .toString();
    }
}
