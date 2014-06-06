package com.github.christophpickl.seetheeye.api;

import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Test
public class MetaClassTest {

    public void getDeclaredConstructors() {
        assertDeclaredConstructorsSize(Empty.class, 1);
        assertDeclaredConstructorsSize(ExplicitDefaultConstructor.class, 1);
        assertDeclaredConstructorsSize(SingleConstructorWithInject.class, 1);
        assertDeclaredConstructorsSize(TwoConstructorsOneWithInjectArgAndOneWithInjectWithoutArgs.class, 2);
    }

    public void getDeclaredConstructorsAnnotatedWith() {
        assertDeclaredConstructorsAnnotatedWithSize(Empty.class, 0);
        assertDeclaredConstructorsAnnotatedWithSize(ExplicitDefaultConstructor.class, 0);
        assertDeclaredConstructorsAnnotatedWithSize(SingleConstructorWithInject.class, 1);
        assertDeclaredConstructorsAnnotatedWithSize(TwoConstructorsOneWithInjectArgAndOneWithInjectWithoutArgs.class, 2);
    }

    private static void assertDeclaredConstructorsAnnotatedWithSize(Class<?> clazz, int expectedSize) {
        assertThat(testee(clazz).getDeclaredConstructorsAnnotatedWith(Inject.class), hasSize(expectedSize));
    }

    private static void assertDeclaredConstructorsSize(Class<?> clazz, int expectedSize) {
        assertThat(testee(clazz).getDeclaredConstructors(), hasSize(expectedSize));
    }

    private static <T> MetaClass<T> testee(Class<T> clazz) {
        return new MetaClass<>(clazz);
    }

    static class Empty { }
    static class ExplicitDefaultConstructor {
        ExplicitDefaultConstructor() {}
    }
    static class SingleConstructorWithInject{
        @Inject SingleConstructorWithInject() {}
    }
    static class TwoConstructorsOneWithInjectArgAndOneWithInjectWithoutArgs {
        @Inject TwoConstructorsOneWithInjectArgAndOneWithInjectWithoutArgs(Object param) {}
        @Inject TwoConstructorsOneWithInjectArgAndOneWithInjectWithoutArgs() {}
    }

}
