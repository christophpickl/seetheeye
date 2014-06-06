package com.github.christophpickl.seetheeye.impl;

@Deprecated
public interface ObserverRepository {

    <T> void dispatch(Class<T> type, T value);

}
