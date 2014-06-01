package com.github.christophpickl.seetheeye.impl;

public enum Scope {

    PROTOTYPE {
        @Override
        public <T> T actOn(ScopeCallback<T> callback) {
            return callback.onPrototype();
        }
    },
    SINGLETON {
        @Override
        public <T> T actOn(ScopeCallback<T> callback) {
            return callback.onSingelton();
        }
    };

    public abstract <T> T actOn(ScopeCallback<T> callback);

    interface ScopeCallback<T> {
        T onPrototype();
        T onSingelton();
    }

}
