package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashSet;

public class BeanDeclaration implements Declaration {

    private static final Logger LOG = LoggerFactory.getLogger(BeanDeclaration.class);

    private static final Scope DEFAULT_SCOPE = Scope.PROTOTYPE;

    private final MetaClass installType;

    private Scope scope;

    private Collection<MetaClass> registrationTypes = new LinkedHashSet<>();

    public BeanDeclaration(MetaClass installType) {
        this.installType = Preconditions.checkNotNull(installType);
        this.scope = installType.isAnnotationPresent(Singleton.class) ? Scope.SINGLETON : DEFAULT_SCOPE;
    }

    public BeanDeclaration addRegistrationType(MetaClass registrationType) {
        registrationTypes.add(Preconditions.checkNotNull(registrationType));
        return this;
    }

    @Override
    public Collection<MetaClass> getRegistrationTypes() {
        return registrationTypes;
    }

    @Override
    public MetaClass getInstallType() {
        return installType;
    }

    public BeanDeclaration setScope(Scope scope) {
        LOG.trace("setScope(scope={}) for installType.name={} (old value={})", scope, installType.getName(), this.scope);
        this.scope = Preconditions.checkNotNull(scope);
        return this;
    }

    // TODO should be internal only
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("installType", installType)
                .add("scope", scope)
                .toString();
    }

}
