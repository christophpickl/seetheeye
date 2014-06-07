package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.*;
import com.github.christophpickl.seetheeye.api.configuration.BeanDeclaration;
import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class ConfigurationValidatorTest {


    @Test public void validatePublicConcreteBeanIsOk() {
        validate(TestableEmptyBean.class);
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
        new ConfigurationValidator().validatePre(Arrays.asList(new ConfigurationDeclaration(new TestableConfiguration(), beans)));
    }

    static class ConcreteStaticNestedClass { }
    class InnerClass { }
    interface InterfaceClass { }
    abstract class AbstractClass { }
}
