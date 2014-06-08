package com.github.christophpickl.seetheeye.api;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MetaClass {

    private final Class<?> enclosedClass;
    private final boolean isAbstract;
    private final boolean isInnerClass;

    public MetaClass(Class<?> enclosedClass) {
        this.enclosedClass = Preconditions.checkNotNull(enclosedClass);
        int modifiers = enclosedClass.getModifiers();
        this.isAbstract = Modifier.isAbstract(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        this.isInnerClass = enclosedClass.getDeclaringClass() != null && !isStatic;
    }

    public Class<?> getEnclosedClass() {
        return enclosedClass;
    }

    public String getName() {
        return enclosedClass.getName();
    }

    public boolean isInterface() {
        return enclosedClass.isInterface();
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public Collection<Constructor> getDeclaredConstructors() {
        return Arrays.asList(enclosedClass.getDeclaredConstructors());
    }

    public Collection<Constructor> getDeclaredConstructorsAnnotatedWith(Class<? extends Annotation> constructorAnnotation) {
        return getDeclaredConstructors().stream().filter(constructor -> constructor.isAnnotationPresent(constructorAnnotation))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public boolean hasOnlySingleConstructor() {
        return getDeclaredConstructors().size() == 1;
    }

    public <T> T instantiate(Constructor<T> constructor, Object... arguments) {
        return ReflectionUtil.instantiate(constructor, arguments);
    }

    public Constructor getSingleConstructor() {
        if (!hasOnlySingleConstructor()) throw new IllegalStateException("Type " + getName() + " does not have one constructor!");
        return getDeclaredConstructors().iterator().next();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return enclosedClass.isAnnotationPresent(annotation);
    }

    public boolean isImplementing(MetaClass checkInterface) {
        if (!checkInterface.isInterface()) {
            throw new IllegalArgumentException("Can only check against interfaces, " +
                "but given type '" + checkInterface.getName() + "' is not an interface!");
        }
        return checkInterface.getEnclosedClass().isAssignableFrom(enclosedClass);
    }

    public MetaClass getSingleTypeParamaterOfSingleInterface() {
        return new MetaClass(ReflectionUtil.extractFirstTypeParameterOfFirstInterface(enclosedClass));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("enclosedClass", enclosedClass.getName())
                .add("isAbstract", isAbstract)
                .add("isInnerClass", isInnerClass)
                .toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaClass that = (MetaClass) o;
        return Objects.equal(this.enclosedClass, that.enclosedClass);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(enclosedClass);
    }

}
