package com.github.christophpickl.seetheeye.impl;

import javax.enterprise.event.Event;

public interface ObserverRepository {

    <T> void dispatch(Class<T> type, T value);

}
