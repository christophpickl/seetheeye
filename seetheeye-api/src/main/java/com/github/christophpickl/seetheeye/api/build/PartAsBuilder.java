package com.github.christophpickl.seetheeye.api.build;

public interface PartAsBuilder<T> {

    T as(Class<?> interfaceType);

}
