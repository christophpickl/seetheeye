package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MetaClass {

    private final Class<?> innerType;
    private final boolean isAbstract;
    private final boolean isInnerClass;

    public MetaClass(Class<?> innerType) {
        this.innerType = innerType;
        int modifiers = innerType.getModifiers();
        this.isAbstract = Modifier.isAbstract(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        this.isInnerClass = innerType.getDeclaringClass() != null && !isStatic;
    }

    public Class<?> getEnclosedClass() {
        return innerType;
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

    public boolean hasOnlySingleConstructor() {
        return getDeclaredConstructors().size() == 1;
    }

    public Constructor getSingleConstructor() {
        if (!hasOnlySingleConstructor()) throw new IllegalStateException("Type " + getName() + " does not have one constructor!");
        return getDeclaredConstructors().iterator().next();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return innerType.isAnnotationPresent(annotation);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("innerType", innerType.getName())
                .add("isAbstract", isAbstract)
                .add("isInnerClass", isInnerClass)
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaClass that = (MetaClass) o;
        return Objects.equal(this.innerType, that.innerType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(innerType);
    }

}
