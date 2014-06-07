package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.InstanceDeclaration;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;

@Test
public class PreValidatorTest {

//    public void installBean_registerAsInterfaceWhichItIsNotImplementing_isInvalid() {
//
//    }

    public void installInstance_registerAsInterfaceWhichItIsNotImplementing_isInvalid() {
        Collection<ConfigurationDeclaration> declarations = new LinkedList<>();

        Collection<BeanDeclaration> beans = Arrays.asList();
        Collection<InstanceDeclaration> instances = Arrays.asList(new InstanceDeclaration(new ClassNotImplementingInterface())
            .addRegistrationType(new MetaClass(Interface.class)));
        declarations.add(new ConfigurationDeclaration(beans, instances));

        assertErrors(declarations, "Given bean '" + ClassNotImplementingInterface.class.getName() + "' does not implement " +
                "interface '" + Interface.class.getName() + "'!");
    }

    private void assertErrors(Collection<ConfigurationDeclaration> declarations, String... errorMessages) {
        Collection<String> errors = new PreValidator(declarations).detect();
        assertThat(errors, hasItems(errorMessages));
    }

    private void assertNoErrors(Collection<ConfigurationDeclaration> declarations) {
        Collection<String> errors = new PreValidator(declarations).detect();
        assertThat(errors, empty());
    }

    static class ClassNotImplementingInterface { }
    interface Interface { }

}
