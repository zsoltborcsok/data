package org.nting.data;

@FunctionalInterface
public interface ValueChangeListener<T> {

    void valueChange(ValueChangeEvent<T> event);

    default int priority() {
        return 1000;
    }
}
