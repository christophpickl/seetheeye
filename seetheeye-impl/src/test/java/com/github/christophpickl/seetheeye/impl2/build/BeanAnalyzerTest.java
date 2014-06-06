package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BeanAnalyzerTest {

    @Test public void findDefaultConstructor() throws Exception {
        assertFoundConstructorFor(Empty.class);
    }

    @Test public void findSingleArgConstructor() throws Exception {
        assertFoundConstructorFor(SingleConstructor.class, Param1.class);
    }

    @Test public void findInjectAnnotatedConstructor() throws Exception {
        assertFoundConstructorFor(SingleConstructorWithInject.class, Param1.class);
    }

    @Test public void findInjectAnnotatedConstructorAmongTwo() throws Exception {
        assertFoundConstructorFor(TwoConstructorsOnlyOneWithInject.class, Param2.class);
    }

    @Test(expectedExceptions = {SeeTheEyeException.InvalidBeanException.class})
    public void findInvalidTwoConstructorsWithInjectShouldThrowInvalidBeanException() throws Exception {
        findConstructor(InvalidTwoConstructorsWithInject.class);
    }

    private void assertFoundConstructorFor(Class<?> type, Class<?>... expectedConstructorArgs) {
        assertThat(findConstructor(type), equalTo(internalFindConstructorByArgs(type, expectedConstructorArgs)));
    }

    private static Constructor findConstructor(Class<?> type) {
        return new BeanAnalyzer().findProperConstructor(new MetaClass(type));
    }


    private static Constructor<?> internalFindConstructorByArgs(Class<?> type, Class<?>... expectedConstructorArgs) {
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (expectedConstructorArgs.length == 0 && constructor.getParameters().length == 0 ||
                    Arrays.equals(constructor.getParameterTypes(), expectedConstructorArgs)) {
                return constructor;
            }
        }
        throw new RuntimeException("Test expection has wrong constructor type params for type " + type.getSimpleName() + "! " +
                "Given expected args: " + Arrays.toString(expectedConstructorArgs));
    }

    static class Empty { }

    static class SingleConstructor {
        SingleConstructor(Param1 param) { }
    }
    static class SingleConstructorWithInject {
        @Inject SingleConstructorWithInject(Param1 param) { }
    }
    static class TwoConstructorsOnlyOneWithInject {
        TwoConstructorsOnlyOneWithInject(Param1 param) { }
        @Inject TwoConstructorsOnlyOneWithInject(Param2 param) { }
    }
    static class InvalidTwoConstructorsWithInject {
        @Inject InvalidTwoConstructorsWithInject(Param1 param) { }
        @Inject InvalidTwoConstructorsWithInject(Param2 param) { }
    }

    static class Param1 {}
    static class Param2 {}

}
