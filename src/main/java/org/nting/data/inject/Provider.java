package org.nting.data.inject;

import java.util.function.Supplier;

public abstract class Provider<T> implements Supplier<T> {

    public static <T> Provider<T> provider(Supplier<T> supplier) {
        return new Provider<T>() {

            @Override
            public T get() {
                return supplier.get();
            }
        };
    }

    public static <T> Provider<T> provider(com.google.common.base.Supplier<T> supplier) {
        return new Provider<T>() {

            @Override
            public T get() {
                return supplier.get();
            }
        };
    }
}
