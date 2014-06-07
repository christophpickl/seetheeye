package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.impl2.build.BeanAnalyzer;
import com.github.christophpickl.seetheeye.impl2.build.ContextFactory;
import com.github.christophpickl.seetheeye.impl2.build.ContextFactoryImpl;
import com.github.christophpickl.seetheeye.impl2.build.SeeTheEyeBuilder;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SeeTheEye implements SeeTheEyeApi {

    private final Context context;

    public SeeTheEye(Context context) {
        this.context = context;
    }

    @Override
    public <T> T get(Class<T> beanType) {
        return context.get(beanType);
    }

    public static SeeTheEyeBuilder builder() {
        Injector injector = Guice.createInjector(new GuiceModule());
        return injector.getInstance(SeeTheEyeBuilder.class);
    }

    static class GuiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(SeeTheEyeBuilder.class);
            bind(BeanAnalyzer.class);
            bind(ConfigurationValidator.class);
            bind(ContextFactory.class).to(ContextFactoryImpl.class);
        }
    }

}
