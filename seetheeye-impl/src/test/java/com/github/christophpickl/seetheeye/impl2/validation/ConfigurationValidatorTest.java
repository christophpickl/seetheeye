package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.InstanceDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ProviderDeclaration;
import com.github.christophpickl.seetheeye.api.Beans;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ConfigurationValidatorTest {


    @Test public void validatePublicConcreteBeanIsOk() {
        validate(Beans.class);
    }

    @Test public void validateConcreteStaticNestedClassIsOk() {
        validate(ConcreteStaticNestedClass.class);
    }


    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void validateInnerClassRequiringOuterInstanceShouldFail() {
        validate(InnerClass.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void validateInterfaceTypeShouldFail() {
        validate(InterfaceClass.class);
    }

    @Test(expectedExceptions = SeeTheEyeException.ConfigInvalidException.class)
    public void validateAbstractClassShouldFail() {
        validate(AbstractClass.class);
    }

    private void validate(Class<?>... beanTypes) {
        Collection<BeanDeclaration> beans = new LinkedList<>();
        for (Class<?> beanType : beanTypes) {
            beans.add(new BeanDeclaration(new MetaClass(beanType)));
        }
        // TODO add testbeans for installInstance and installProvider
        Collection<InstanceDeclaration> instances = Collections.emptyList();
        Collection<ProviderDeclaration> providers = Collections.emptyList();
        new ConfigurationValidator().validatePre(Arrays.asList(new ConfigurationDeclaration(beans, instances, providers)));
    }

    static class ConcreteStaticNestedClass { }
    class InnerClass { }
    interface InterfaceClass { }
    abstract class AbstractClass { }
}
