package com.github.christophpickl.seetheeye.impl;

import com.google.inject.AbstractModule;

@Deprecated
public class SeeTheEyeGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeeTheEyeBuilder.class);
    }

}
