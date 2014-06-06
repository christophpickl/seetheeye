package com.github.christophpickl.seetheeye.impl2.build;

import com.github.christophpickl.seetheeye.api.MetaClass;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.lang.reflect.Constructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BeanAnalyzerTest {

    @Test public void findDefaultConstructor() throws Exception {
        assertFoundConstructorFor(Empty.class, Empty.class.getDeclaredConstructors()[0]);
    }

    @Test public void findSingleArgConstructor() throws Exception {
        assertFoundConstructorFor(SingleConstructor.class, SingleConstructor.class.getDeclaredConstructors()[0]);
    }

    @Test public void findInjectAnnotatedConstructor() throws Exception {
        assertFoundConstructorFor(SingleConstructorWithInject.class, SingleConstructorWithInject.class.getDeclaredConstructors()[0]);
    }

    @Test(expectedExceptions = {SeeTheEyeException.InvalidBeanException.class})
    public void findInvalidTwoConstructorsWithInjectShouldThrowInvalidBeanException() throws Exception {
        findConstructor(InvalidTwoConstructorsWithInject.class);
    }

    private void assertFoundConstructorFor(Class<?> type, Constructor expected) {
        assertThat(findConstructor(type), equalTo(expected));
    }

    private Constructor findConstructor(Class<?> type) {
        return new BeanAnalyzer().findConstructor(new MetaClass<>(type));
    }

    static class Empty { }

    static class SingleConstructor {
        SingleConstructor(Object param) { }
    }
    static class SingleConstructorWithInject {
        @Inject SingleConstructorWithInject(Object param) { }
    }
    static class InvalidTwoConstructorsWithInject {
        @Inject InvalidTwoConstructorsWithInject(Integer param) { }
        @Inject InvalidTwoConstructorsWithInject(String param) { }
    }

}
