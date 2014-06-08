package com.github.christophpickl.seetheeye.api;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Test
public class ReflectionUtilTest {

    public void extract_classImplementingComparableWithTypeString_returnsString() {
        assertThat(ReflectionUtil.extractFirstTypeParameterOfFirstInterface(ImplementingComparableString.class),
            equalTo(String.class));
    }
    @Test(expectedExceptions = ReflectionException.InvalidTypeException.class,
        expectedExceptionsMessageRegExp = "Class com.github.christophpickl.seetheeye.api.Beans\\$Empty must implement " +
            "exactly one generic interface but implements: 0\\!")
    public void extract_classNotImplementingAnyInterface_throwsExceptionBecauseMustImplementOne() {
        ReflectionUtil.extractFirstTypeParameterOfFirstInterface(Beans.Empty.class);
    }

    @Test(expectedExceptions = ReflectionException.InvalidTypeException.class,
        expectedExceptionsMessageRegExp = "Class com.github.christophpickl.seetheeye.api.ReflectionUtilTest\\$ImplementingComparableAndRunnable " +
            "must implement exactly one generic interface but implements: 2\\!")
    public void extract_classImplementingTwoInterfaces_throwsExceptionBecauseMustImplementOne() {
        ReflectionUtil.extractFirstTypeParameterOfFirstInterface(ImplementingComparableAndRunnable.class);
    }

    @Test(expectedExceptions = ReflectionException.InvalidTypeException.class,
        expectedExceptionsMessageRegExp = "Expected single interface to be parametrized \\(of typeParameterizedTypeImpl\\) but was: java.lang.Class")
    public void extract_classImplementingOneNonGenericInterface_throwsExceptionBecauseInterfaceMustBeGeneric() {
        ReflectionUtil.extractFirstTypeParameterOfFirstInterface(ImplementingRunnable.class);
    }

    @Test(expectedExceptions = ReflectionException.InvalidTypeException.class,
        expectedExceptionsMessageRegExp = "Class com.github.christophpickl.seetheeye.api.ReflectionUtilTest\\$TwoParamsImpl " +
            "with generic interface of com.github.christophpickl.seetheeye.api.ReflectionUtilTest.com.github.christophpickl.seetheeye.api.ReflectionUtilTest\\$TwoParams<java.lang.String, java.lang.Integer> " +
            "must have exactly one type argument but has: 2\\!")
    public void extract_classImplementingInterfaceWithTwoParams_throwsException() {
        ReflectionUtil.extractFirstTypeParameterOfFirstInterface(TwoParamsImpl.class);
    }

    static class ImplementingComparableAndRunnable implements Comparable<Object>, Runnable {
        @Override public int compareTo(Object o) { return 0; }
        @Override public void run() { }
    }

    static class ImplementingRunnable implements Runnable {
        @Override public void run() { }
    }


    static class ImplementingComparableString implements Comparable<String> {
        @Override public int compareTo(String o) { return 0; }
    }

    interface TwoParams<P, K> { }
    static class TwoParamsImpl implements TwoParams<String, Integer> { }

}
