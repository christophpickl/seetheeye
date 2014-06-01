package com.github.christophpickl.seetheeye.api;

public interface SeeTheEyeApi {

    <T> T get(Class<T> beanType);
}
