package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.MetaClass;

public interface DefinitionX<T> {

    MetaClass<?> getRegistrationType();

    T instance();
}
