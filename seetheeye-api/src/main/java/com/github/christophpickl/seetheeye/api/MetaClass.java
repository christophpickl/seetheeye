package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MetaClass<T> {

    private final Class<T> innerType;
    private final boolean isAbstract;
    private final boolean isInnerClass;

    public MetaClass(Class<T> innerType) {
        this.innerType = innerType;
        int modifiers = innerType.getModifiers();
        this.isAbstract = Modifier.isAbstract(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        this.isInnerClass = innerType.getDeclaringClass() != null && !isStatic;
    }

    public Class<T> getInnerType() {
        return innerType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("innerType", innerType)
                .toString();
    }

    public String getName() {
        return innerType.getName();
    }

    public boolean isInterface() {
        return innerType.isInterface();
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public Collection<Constructor> getDeclaredConstructors() {
        return Arrays.asList(innerType.getDeclaredConstructors());
    }

    public Collection<Constructor> getDeclaredConstructorsAnnotatedWith(Class<? extends Annotation> constructorAnnotation) {
        return getDeclaredConstructors().stream().filter(constructor -> constructor.isAnnotationPresent(constructorAnnotation))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
