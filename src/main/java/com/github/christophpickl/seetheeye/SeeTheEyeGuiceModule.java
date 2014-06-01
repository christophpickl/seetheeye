package com.github.christophpickl.seetheeye;

import com.google.inject.AbstractModule;

public class SeeTheEyeGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SeeTheEyeBuilder.class);
    }

}
