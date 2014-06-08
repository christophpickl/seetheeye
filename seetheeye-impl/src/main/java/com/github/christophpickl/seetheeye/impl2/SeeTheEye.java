package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.impl2.build.*;
import com.github.christophpickl.seetheeye.impl2.build.ResolverFactoryImpl;
import com.github.christophpickl.seetheeye.impl2.build.ResolverFactory;
import com.github.christophpickl.seetheeye.impl2.validation.ConfigurationValidator;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class SeeTheEye implements SeeTheEyeApi {

    private final Resolver resolver;

    public SeeTheEye(Resolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public <T> T get(Class<T> beanType) {
        return resolver.get(beanType);
    }

    @Override
    public <T> T getGeneric(Class<T> beanType, Class<?>... typeArguments) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public static SeeTheEyeBuilder builder() {
        Injector injector = Guice.createInjector(new GuiceModule());
        return injector.getInstance(SeeTheEyeBuilder.class);
    }

    static class GuiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(SeeTheEyeBuilder.class);
            bind(TypeAnalyzer.class);
            bind(ConfigurationValidator.class);
            bind(BeanDefinitionMapper.class);
            bind(InstanceDefinitionMapper.class);
            bind(ProviderDefinitionMapper.class);
            bind(ResolverFactory.class).to(ResolverFactoryImpl.class);
        }
    }

}
