package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.*;
import com.github.christophpickl.seetheeye.api.Beans;
import org.testng.annotations.Test;

import javax.inject.Provider;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;

@Test
public class PreValidatorTest {

//    public void installBean_registerAsInterfaceWhichItIsNotImplementing_isInvalid() {
//
//    }

    // TODO more validator tests

    public void installInstance_registerAsInterfaceWhichItIsNotImplementing_isInvalid() {
        assertErrors(new ConfigDeclarationBuilder().addInstance(
            new InstanceDeclaration(new ClassNotImplementingInterface()).addRegistrationType(new MetaClass(Interface.class))
        ), "Given bean '" + ClassNotImplementingInterface.class.getName() + "' does not implement " +
                "interface '" + Interface.class.getName() + "'!");
    }

    public void installProvider_providerNotImplementingProviderInterfaceAtAll_isInvalid() {
        assertErrors(new ConfigDeclarationBuilder().addProvider(
            new ProviderDeclaration((Class) Beans.Empty.class)
        ), "Configured provider does not implement the javax.inject.Provider interface!");
    }


    public void installBeanAndProvider_bothOfTheSameType_isInvalid() {
        assertErrors(new ConfigDeclarationBuilder()
            .addBean(new BeanDeclaration(new MetaClass(Beans.Empty.class)))
            .addProvider(new ProviderDeclaration(EmptyProvider.class))
            , "Duplicate bean definition for 'com.github.christophpickl.seetheeye.api.Beans$Empty'!");
    }

    public void installInstanceAndBean_bothOfTheSameType_isInvalid() {
        assertErrors(new ConfigDeclarationBuilder()
            .addInstance(new InstanceDeclaration(new Beans.Empty()))
            .addBean(new BeanDeclaration(new MetaClass(Beans.Empty.class)))
            , "Duplicate bean definition for 'com.github.christophpickl.seetheeye.api.Beans$Empty'!");
    }

    public void installInstanceAndProivder_bothOfTheSameType_isInvalid() {
        assertErrors(new ConfigDeclarationBuilder()
            .addInstance(new InstanceDeclaration(new Beans.Empty()))
            .addProvider(new ProviderDeclaration(EmptyProvider.class))
            , "Duplicate bean definition for 'com.github.christophpickl.seetheeye.api.Beans$Empty'!");
    }

    private void assertErrors(Collection<ConfigDeclarationBuilder> builders, String... errorMessages) {
        List<ConfigurationDeclaration> declarations = builders.stream().map(builder -> builder.build()).collect(Collectors.toList());
        Collection<String> errors = new PreValidator(declarations).detect();
        assertThat(errors, hasItems(errorMessages));
    }

    private void assertErrors(ConfigDeclarationBuilder builder, String... errorMessages) {
        assertErrors(Arrays.asList(builder), errorMessages);
    }

    private void assertNoErrors(Collection<ConfigurationDeclaration> declarations) {
        Collection<String> errors = new PreValidator(declarations).detect();
        assertThat(errors, empty());
    }

    static class ClassNotImplementingInterface { }
    interface Interface { }

    static class EmptyProvider implements Provider<Beans.Empty> {
        @Override public Beans.Empty get() { return null; }
    }

}
