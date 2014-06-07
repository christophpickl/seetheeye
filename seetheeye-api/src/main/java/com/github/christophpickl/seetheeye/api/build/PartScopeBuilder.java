package com.github.christophpickl.seetheeye.api.build;

import com.github.christophpickl.seetheeye.api.configuration.Scope;

public interface PartScopeBuilder<T> {

    T in(Scope scope);

}
