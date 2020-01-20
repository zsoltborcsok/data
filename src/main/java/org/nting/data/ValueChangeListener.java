package org.nting.data;

@FunctionalInterface
public interface ValueChangeListener<T> {

    void valueChange(ValueChangeEvent<T> event);

    default int priority() {
        return 1000;
    }

    default ValueChangeListener<T> withPriority(int priority) {
        return new ValueChangeListener<T>() {

            @Override
            public void valueChange(ValueChangeEvent<T> event) {
                ValueChangeListener.this.valueChange(event);
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }
}
