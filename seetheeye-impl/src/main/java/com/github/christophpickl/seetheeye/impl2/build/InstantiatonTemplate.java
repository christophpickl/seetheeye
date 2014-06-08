package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class InstantiatonTemplate<T> {

    private final MetaClass type;
    private final Constructor<T> constructor;
    private final Collection<MetaClass> dependencies;

    public InstantiatonTemplate(MetaClass type, Constructor constructor, Collection<MetaClass> dependencies) {
        this.type = type;
        this.constructor = constructor;
        this.dependencies = dependencies;
    }

    public MetaClass getType() {
        return type;
    }

    public Collection<MetaClass> getDependencies() {
        return dependencies;
    }

    public T instantiate(Collection<Object> arguments) {
        return type.instantiate(constructor, arguments);
    }
}
