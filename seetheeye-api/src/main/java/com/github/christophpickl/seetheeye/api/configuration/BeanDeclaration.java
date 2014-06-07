package com.github.christophpickl.seetheeye.api.configuration;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashSet;

public class BeanDeclaration {

    private static final Logger LOG = LoggerFactory.getLogger(BeanDeclaration.class);

    private static final Scope DEFAULT_SCOPE = Scope.PROTOTYPE;

    private final MetaClass beanType;

    private Scope scope;

    private Collection<MetaClass> registrationTypes = new LinkedHashSet<>();

    public BeanDeclaration(MetaClass beanType) {
        this.beanType = Preconditions.checkNotNull(beanType);
        this.scope = beanType.isAnnotationPresent(Singleton.class) ? Scope.SINGLETON : DEFAULT_SCOPE;
    }

    public void addRegistrationType(MetaClass registrationType) {
        registrationTypes.add(registrationType);
    }

    public Collection<MetaClass> getRegistrationTypes() {
        return registrationTypes;
    }

    public MetaClass getBeanType() {
        return beanType;
    }

    public void setScope(Scope scope) {
        LOG.trace("setScope(scope={}) for beanType.name={} (old value={})", scope, beanType.getName(), this.scope);
        this.scope = Preconditions.checkNotNull(scope);
    }

    // TODO should be internal only
    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("beanType", beanType)
                .add("scope", scope)
                .toString();
    }

}
